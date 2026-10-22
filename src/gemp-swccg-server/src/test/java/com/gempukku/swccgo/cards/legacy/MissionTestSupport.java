package com.gempukku.swccgo.cards.legacy;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.AbstractMission;
import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.AbstractSystem;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.gempukku.swccgo.game.layout.DefaultPlanetLayout;
import com.gempukku.swccgo.game.layout.LocationLayout;
import com.gempukku.swccgo.game.layout.LocationsLayout;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * Test-only blueprints for Mission hosts and locations that are not on this master.
 */
public final class MissionTestSupport {
    private MissionTestSupport() {
    }

    public static AbstractNormalEffect warHasBegun() {
        return new AbstractNormalEffect(Side.DARK, 0, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "War Has Begun", Uniqueness.UNIQUE, ExpansionSet.PREMIERE, Rarity.C1) {
        };
    }

    public static AbstractNormalEffect begunTheCloneWarHas() {
        return new AbstractNormalEffect(Side.LIGHT, 0, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Begun, The Clone War Has", Uniqueness.UNIQUE, ExpansionSet.PREMIERE, Rarity.C1) {
        };
    }

    public static AbstractSite isbCentralOffice() {
        return new AbstractSite(Side.DARK, "Coruscant: ISB Central Office", "Coruscant", Uniqueness.UNIQUE, ExpansionSet.PREMIERE, Rarity.C1) {
            {
                addIcon(Icon.DARK_FORCE, 1);
                addIcon(Icon.LIGHT_FORCE, 1);
                addIcons(Icon.INTERIOR_SITE, Icon.PLANET, Icon.SCOMP_LINK);
            }
        };
    }

    public static AbstractImperial isbAgent() {
        return new AbstractImperial(Side.DARK, 1, 1, 1, 1, 2, "ISB Test Agent", Uniqueness.UNRESTRICTED, ExpansionSet.PREMIERE, Rarity.C1) {
            {
                addKeywords(Keyword.ISB_AGENT);
            }
        };
    }

    public static AbstractSystem namedSystem(String title, int parsec) {
        return new AbstractSystem(Side.DARK, title, parsec, ExpansionSet.PREMIERE, Rarity.C1) {
            {
                addIcon(Icon.DARK_FORCE, 1);
                addIcon(Icon.LIGHT_FORCE, 1);
                addIcons(Icon.PLANET);
            }
        };
    }

    public static AbstractSystem muunilinstSystem() {
        return namedSystem("Muunilinst", 4);
    }

    public static AbstractSite muunilinstSite(String title) {
        return muunilinstSite(Side.DARK, title);
    }

    public static AbstractSite muunilinstSite(Side side, String title) {
        return new AbstractSite(side, title, "Muunilinst", Uniqueness.UNIQUE, ExpansionSet.PREMIERE, Rarity.C1) {
            {
                addIcon(Icon.DARK_FORCE, 1);
                addIcon(Icon.LIGHT_FORCE, 1);
                addIcons(Icon.EXTERIOR_SITE, Icon.PLANET);
            }
        };
    }

    /**
     * Test-only Geonosis: Petranaki Arena for the given side (GEMP has no Light side Petranaki Arena yet).
     * Used to exercise "raise your converted Petranaki Arena" game text.
     */
    public static AbstractSite geonosisPetranakiArena(Side side) {
        return new AbstractSite(side, Title.Geonosis_Petranaki_Arena, Title.Geonosis, Uniqueness.UNIQUE, ExpansionSet.PREMIERE, Rarity.C1) {
            {
                addIcon(Icon.DARK_FORCE, 1);
                addIcon(Icon.LIGHT_FORCE, 2);
                addIcons(Icon.EXTERIOR_SITE, Icon.PLANET, Icon.EPISODE_I);
            }
        };
    }

    public static AbstractNormalEffect tieFighterConstructionFacility() {
        return new AbstractNormalEffect(Side.DARK, 3, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "TIE Fighter Construction Facility", Uniqueness.UNIQUE, ExpansionSet.PREMIERE, Rarity.C1) {
        };
    }

    public static AbstractNormalEffect closeAirSupport() {
        return new AbstractNormalEffect(Side.LIGHT, 3, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Close Air Support", Uniqueness.UNIQUE, ExpansionSet.PREMIERE, Rarity.C1) {
        };
    }

    public static AbstractNormalEffect arenaPillars() {
        return new AbstractNormalEffect(Side.DARK, 3, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Arena Pillars", Uniqueness.UNIQUE, ExpansionSet.PREMIERE, Rarity.C1) {
        };
    }

    public static AbstractNormalEffect vb2SpecialDelivery() {
        return new AbstractNormalEffect(Side.DARK, 4, PlayCardZoneOption.ATTACHED, "Special Delivery", Uniqueness.UNIQUE, ExpansionSet.PREMIERE, Rarity.C1) {
            {
                addIcons(Icon.LEGACY_BLOCK_2);
                addKeywords(Keyword.DEPLOYS_ON_SITE);
            }

            @Override
            protected com.gempukku.swccgo.filters.Filter getGameTextValidDeployTargetFilter(com.gempukku.swccgo.game.SwccgGame game, com.gempukku.swccgo.game.PhysicalCard self, com.gempukku.swccgo.common.PlayCardOptionId playCardOptionId, boolean asReact) {
                return com.gempukku.swccgo.filters.Filters.prison;
            }
        };
    }

    public static PhysicalCardImpl putInReserve(VirtualTableScenario scn, int id, String owner, SwccgCardBlueprint blueprint) {
        PhysicalCardImpl card = create(scn, id, owner, blueprint);
        scn.gameState().addCardToZone(card, Zone.RESERVE_DECK, owner, true, false, false);
        return card;
    }

    public static AbstractMission testCritical(Side side, String title) {
        return new AbstractMission(side, 0, title, Uniqueness.UNRESTRICTED, ExpansionSet.PREMIERE, Rarity.C1, true) {
        };
    }

    @SuppressWarnings("unchecked")
    public static PhysicalCardImpl create(VirtualTableScenario scn, int id, String owner, SwccgCardBlueprint blueprint) {
        String blueprintId = "2000_" + id;
        try {
            Field field = SwccgCardBlueprintLibrary.class.getDeclaredField("_blueprintMap");
            field.setAccessible(true);
            Map<String, SwccgCardBlueprint> map = (Map<String, SwccgCardBlueprint>) field.get(VirtualTableScenario._cardLibrary);
            map.put(blueprintId, blueprint);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
        return (PhysicalCardImpl) scn.gameState().createPhysicalCard(owner, VirtualTableScenario._cardLibrary, blueprintId);
    }

    public static PhysicalCardImpl putInHand(VirtualTableScenario scn, int id, String owner, SwccgCardBlueprint blueprint) {
        PhysicalCardImpl card = create(scn, id, owner, blueprint);
        scn.gameState().addCardToZone(card, Zone.HAND, owner, true, false, false);
        return card;
    }

    public static PhysicalCardImpl putOnTable(VirtualTableScenario scn, int id, String owner, SwccgCardBlueprint blueprint) {
        PhysicalCardImpl card = create(scn, id, owner, blueprint);
        scn.gameState().addCardToZone(card, Zone.SIDE_OF_TABLE, owner, true, false, false);
        return card;
    }

    public static PhysicalCardImpl putLocationOnTable(VirtualTableScenario scn, int id, String owner, SwccgCardBlueprint blueprint) {
        String title = blueprint.getTitle();
        if (title != null && title.startsWith("Muunilinst")) {
            ensurePlanetLayout(scn, "Muunilinst", 4);
        }
        if (title != null && title.startsWith("Coruscant")) {
            ensurePlanetLayout(scn, "Coruscant", 0);
        }
        if (title != null && title.startsWith(Title.Geonosis)) {
            ensurePlanetLayout(scn, Title.Geonosis, 6);
        }
        if (blueprint instanceof AbstractSystem) {
            ensurePlanetLayout(scn, blueprint.getTitle(), blueprint.getParsec());
        }
        PhysicalCardImpl card = create(scn, id, owner, blueprint);
        scn.MoveLocationToTable(card);
        return card;
    }

    @SuppressWarnings("unchecked")
    public static void ensurePlanetLayout(VirtualTableScenario scn, String systemName, int parsec) {
        try {
            Field field = LocationsLayout.class.getDeclaredField("_locationLayouts");
            field.setAccessible(true);
            List<LocationLayout> layouts = (List<LocationLayout>) field.get(scn.gameState().getLocationsLayout());
            for (LocationLayout layout : layouts) {
                if (systemName.equals(layout.getParentSystemTitle())) {
                    return;
                }
            }
            layouts.add(new DefaultPlanetLayout(systemName, parsec));
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
