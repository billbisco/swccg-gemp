package com.gempukku.swccgo.cards.set1263.dark;

import com.gempukku.swccgo.cards.AbstractCombatVehicle;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.conditions.AloneCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfBattleModifierEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.HitResult;
import java.util.Collections;
import java.util.List;

/** Legacy Block 6.3: Droid Walker. */
public class Card1263_021 extends AbstractCombatVehicle {
    public Card1263_021() {
        super(Side.DARK, 2, 3, 4, 4, null, 2, 5, "Droid Walker", Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_BLOCK_6_3, Rarity.V);
        setLore("Enclosed.");
        setGameText("Deploys -1 to same site as your MTT. AT-AT Cannon may deploy aboard, and targets it hits may not be used to satisfy attrition. Unless alone, draws one battle destiny if not able to otherwise.");
        addIcons(Icon.EPISODE_I, Icon.PILOT, Icon.PRESENCE, Icon.A_NEW_HOPE, Icon.LEGACY_BLOCK_6);
        addKeyword(Keyword.ENCLOSED);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot() {});
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        return Collections.singletonList(new DeployCostToLocationModifier(self, -1,
                Filters.sameSiteAs(self, Filters.and(Filters.your(self), Filters.MTT))));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        return List.of(new MayDeployToTargetModifier(self, Filters.and(Filters.your(self), Filters.AT_AT_Cannon), self),
                new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, new NotCondition(new AloneCondition(self)), 1));
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.justHitBy(game, effectResult, Filters.any, Filters.and(Filters.AT_AT_Cannon, Filters.attachedTo(self)))) {
            PhysicalCard hit = ((HitResult) effectResult).getCardHit();
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Hit target may not satisfy attrition");
            action.appendEffect(new AddUntilEndOfBattleModifierEffect(action, new MayNotBeUsedToSatisfyAttritionModifier(self, hit),
                    "Hit target may not be used to satisfy attrition"));
            return Collections.singletonList(action);
        }
        return null;
    }
}
