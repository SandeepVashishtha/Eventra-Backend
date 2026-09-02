package com.sandeep.eventrabackend.repository;

import com.sandeep.eventrabackend.model.AddonInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository interface providing atomic database updates for add-on inventory (#19087).
 */
@Repository
public interface AddonInventoryRepository extends JpaRepository<AddonInventory, String> {

    /**
     * Atomically decrements remaining add-on count by 1 if current count is greater than 0.
     * Prevents race conditions and overselling across concurrent requests and multiple instances.
     *
     * @param addonId The unique identifier of the add-on.
     * @return The number of rows updated (1 if successfully decremented, 0 if quota is depleted).
     */
    @Modifying
    @Query("UPDATE AddonInventory a SET a.remaining = a.remaining - 1 WHERE a.id = :addonId AND a.remaining > 0")
    int decrementRemainingIfPositive(@Param("addonId") String addonId);

    /**
     * Atomically inserts an add-on inventory record if it does not already exist.
     * Uses native SQL with conflict handling to prevent check-then-insert race conditions
     * across horizontally-scaled instances starting concurrently.
     *
     * @param addonId The unique identifier of the add-on.
     * @param remaining The initial quota count.
     * @return The number of rows inserted (1 if inserted, 0 if already existed).
     */
    @Modifying
    @Query(value = "INSERT INTO addon_inventory (id, remaining) VALUES (:addonId, :remaining) ON CONFLICT (id) DO NOTHING", nativeQuery = true)
    int insertIfAbsent(@Param("addonId") String addonId, @Param("remaining") int remaining);
}
