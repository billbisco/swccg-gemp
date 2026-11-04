package com.gempukku.swccgo.cards.set1291.light;

import com.gempukku.swccgo.cards.AbstractCombatVehicle;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.ManeuverModifier;
import com.gempukku.swccgo.logic.modifiers.MayBeTargetedByWeaponsLikeStarfighterModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployAsReactToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 9.1
 * Type: Vehicle
 * Subtype: Combat
 * Title: Republic Gunship Wing
 */
public class Card1291_074 extends AbstractCombatVehicle {
    public Card1291_074() {
        super(Side.LIGHT, 3, 6, 6, null, 5, 3, 6, "Republic Gunship Wing", Uniqueness.RESTRICTED_2, ExpansionSet.VIRTUAL_BLOCK_9_1, Rarity.V);
        setLore("Enclosed.");
        setGameText("Permanent pilots provide total ability of 6. At cloud sectors: power and maneuver +3, may deploy as a 'react', and may move and be targeted by weapons like a starfighter. Immune to attrition < 5.");
        addIcon(Icon.PILOT, 3);
        addIcons(Icon.CLONE_ARMY);
        addModelType(ModelType.LAAT);
        addKeywords(Keyword.ENCLOSED);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        List<AbstractPermanentAboard> permanentsAboard = new ArrayList<AbstractPermanentAboard>();
        permanentsAboard.add(new AbstractPermanentPilot(2) {});
        permanentsAboard.add(new AbstractPermanentPilot(2) {});
        permanentsAboard.add(new AbstractPermanentPilot(2) {});
        return permanentsAboard;
    }

    // "At cloud sectors: ... may deploy as a 'react'": a cloud sector is a deploy target only for a 'react'.
    @Override
    public boolean isDeploysLikeStarfighterAtCloudSectors() {
        return true;
    }

    @Override
    public boolean isMovesLikeStarfighterAtCloudSectors() {
        return true;
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return asReact ? Filters.any : Filters.not(Filters.cloud_sector);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployAsReactToLocationModifier(self, Filters.cloud_sector));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition atCloudSector = new AtCondition(self, Filters.cloud_sector);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, atCloudSector, 3));
        modifiers.add(new ManeuverModifier(self, atCloudSector, 3));
        modifiers.add(new MayBeTargetedByWeaponsLikeStarfighterModifier(self, atCloudSector));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 5));
        return modifiers;
    }
}
