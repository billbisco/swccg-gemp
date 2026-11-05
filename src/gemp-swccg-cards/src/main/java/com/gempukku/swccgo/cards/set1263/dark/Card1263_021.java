package com.gempukku.swccgo.cards.set1263.dark;

import com.gempukku.swccgo.cards.AbstractCombatVehicle;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.conditions.AloneCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeUsedToSatisfyAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.HitResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 6.3
 * Type: Vehicle
 * Subtype: Combat
 * Title: Droid Walker
 */
public class Card1263_021 extends AbstractCombatVehicle {
    public Card1263_021() {
        super(Side.DARK, 2, 3, 4, 4, null, 2, 5, "Droid Walker", Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_BLOCK_6_3, Rarity.V);
        setLore("Enclosed.");
        setGameText("Deploys -1 to same site as your MTT. AT-AT Cannon may deploy aboard, and targets it hits may not be used to satisfy attrition. Unless alone, draws one battle destiny if not able to otherwise.");
        addIcons(Icon.A_NEW_HOPE, Icon.EPISODE_I, Icon.PILOT, Icon.PRESENCE);
        addKeywords(Keyword.ENCLOSED);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot() {});
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, -1, Filters.sameSiteAs(self, Filters.and(Filters.your(self), Filters.MTT))));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployToTargetModifier(self, Filters.and(Filters.your(self), Filters.AT_AT_Cannon), self));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, new NotCondition(new AloneCondition(self)), 1));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.justHitBy(game, effectResult, Filters.any, Filters.AT_AT_Cannon, self)) {
            PhysicalCard cardHit = ((HitResult) effectResult).getCardHit();

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Make " + GameUtils.getFullName(cardHit) + " unable to satisfy attrition");
            action.setActionMsg("Make " + GameUtils.getCardLink(cardHit) + " unable to be used to satisfy attrition");
            // Perform result(s)
            action.appendEffect(
                    new AddUntilEndOfTurnModifierEffect(action,
                            new MayNotBeUsedToSatisfyAttritionModifier(self, Filters.and(cardHit, Filters.hit)),
                            "Makes " + GameUtils.getCardLink(cardHit) + " unable to be used to satisfy attrition"));
            return Collections.singletonList(action);
        }
        return null;
    }
}
