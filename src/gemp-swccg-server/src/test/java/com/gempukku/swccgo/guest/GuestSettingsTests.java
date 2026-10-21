package com.gempukku.swccgo.guest;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GuestSettingsTests {

    @Test
    public void GuestSettingsDefaultToOffWithBillsCaps() {
        //test1: guest play is off unless switched on
        //test2: the caps are the agreed starting values
        GuestSettings s = GuestSettings.defaults();
        assertFalse(s.enabled()); //test1
        assertEquals(20, s.maxConcurrentGames()); //test2
        assertEquals(2, s.maxConcurrentPerIp()); //test2
        assertEquals(10, s.startsPerIpPerHour()); //test2
        assertEquals(120, s.startsPerHourGlobal()); //test2
        assertEquals(15, s.unloadIdleMinutes()); //test2
        assertEquals(30, s.capEvictIdleMinutes()); //test2
        assertEquals(15, s.saveRetentionDays()); //test2
        assertTrue(s.ipAllowlist().isEmpty()); //test2
    }

    @Test
    public void GuestSettingsAreReadFromConfig() {
        //test1: every value can be set
        //test2: a bad number falls back to the default instead of failing startup
        Map<String, String> p = new HashMap<>();
        p.put("guest.enabled", " true ");
        p.put("guest.maxConcurrentGames", "40");
        p.put("guest.maxConcurrentPerIp", "3");
        p.put("guest.startsPerIpPerHour", "5");
        p.put("guest.startsPerHourGlobal", "60");
        p.put("guest.ipAllowlist", "1.2.3.4, 5.6.7.8,,");
        p.put("guest.unloadIdleMinutes", "10");
        p.put("guest.capEvictIdleMinutes", "45");
        p.put("guest.saveRetentionDays", "abc");
        GuestSettings s = GuestSettings.parse(p::get);
        assertTrue(s.enabled()); //test1
        assertEquals(40, s.maxConcurrentGames()); //test1
        assertEquals(3, s.maxConcurrentPerIp()); //test1
        assertEquals(5, s.startsPerIpPerHour()); //test1
        assertEquals(60, s.startsPerHourGlobal()); //test1
        assertEquals(Set.of("1.2.3.4", "5.6.7.8"), s.ipAllowlist()); //test1
        assertEquals(10, s.unloadIdleMinutes()); //test1
        assertEquals(45, s.capEvictIdleMinutes()); //test1
        assertEquals(15, s.saveRetentionDays()); //test2
    }

    @Test
    public void GuestSettingsInThePropertiesFileAreOff() {
        //test1: the shipped gemp-swccg.properties leaves guest play off
        assertFalse(GuestSettings.fromConfiguration().enabled()); //test1
    }
}
