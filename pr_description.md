# 🚀 Implement Event Availability and Registration API with Concurrency Handling

**Closes Issues:** #2101, #2102, #2104

## 🧩 Problem Solved
- **#2101 (Availability):** Needed a way for frontends to accurately check if an event has spots available, how many are left, and if the event has passed, without needing authentication.
- **#2102 (Registration):** Needed an authenticated API endpoint to register users for events, enforcing validation (duplicate registrations, capacity) and returning a clean response.
- **#2104 (Concurrency):** Concurrent registrations could overbook an event beyond its capacity, leading to data inconsistency and a poor user experience.

## 💡 Approach
1. **Event Model & Optimistic Locking (`Event.java`)**
   - Added a `@Version` field to the `Event` entity for optimistic locking. This acts as a safety net if a race condition sneaks past the pessimistic lock.
   - Cleaned up redundant fields (`maxAttendees`, `currentAttendees`), as they duplicated the existing `capacity` and `registeredCount`.
   - Added an `isEventPast()` helper to let the frontend know an event has passed.

2. **Availability API (`EventAvailabilityResponse.java` & `EventController.java`)**
   - Opened `GET /api/events/{id}/availability` to public access in `SecurityConfig`.
   - Populated the response with `maxAttendees`, `currentAttendees`, and `availabilityStatus` as per #2101 requirements (implemented via `@JsonProperty` aliases for backward compatibility).
   - Added an `eventPassed` flag to offload logic for "past events" directly to the frontend.

3. **Registration API (`RegistrationResponse.java` & `EventController.java`)**
   - Created a clean `RegistrationResponse` DTO to prevent leaking internal entity fields (like full attendee lists) to the client.
   - Updated `EventService.registerUserForEvent()` to handle business validation and return a 409 Conflict if a user is already registered (throws `RegistrationConflictException`) or if the event is full (`EventFullException`).
   - Fixed `SecurityConfig` to return a `401 Unauthorized` instead of the default `403 Forbidden` on missing/invalid JWT tokens.

4. **Concurrency Handling (`EventService.java`)**
   - Implemented a "belt-and-suspenders" concurrency model:
     - **Pessimistic write lock** (`SELECT ... FOR UPDATE`) on the `Event` row to serialize concurrent registration requests.
     - **Retry Loop**: Wrapped the registration transaction in a retry loop (max 3 retries) for `ObjectOptimisticLockingFailureException`. If transient contention causes an optimistic lock failure, it transparently retries. If all retries fail, a user-friendly 409 error is returned.

5. **Error Handling (`GlobalExceptionHandler.java`)**
   - Added a handler for `EventFullException` to properly return `409 Conflict` instead of `500 Internal Server Error`.

## 🧪 Testing
- **Unit & Integration Tests:** 
  - `EventRegistrationTests.java`: Added/updated 10 test cases covering public availability access, JSON payload accuracy, unauthorized registration blocks, duplicate registration blocks, and event full rejection.
  - `EventRegistrationConcurrencyIntegrationTest.java`: Added invariant-based concurrency tests (e.g., verifying that exactly `capacity` registrations succeed out of `2 * capacity` threads racing).
- **Test Results:** 13/13 tests passing successfully in the local build (`mvn clean test`).

## 🛠 Follows Code Standards
- Java code formatting is clean, well-commented with Javadocs explaining complex concurrency logic.
- Proper use of DTOs vs Entities in controllers.
- Commit conventions outlined in `CONTRIBUTING.md` are supported (changes broken down logically).
