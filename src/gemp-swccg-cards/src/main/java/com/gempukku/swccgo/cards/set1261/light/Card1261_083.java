package com.gempukku.swccgo.cards.set1261.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/** Mon Calamari Dockyards, final Virtual Block 6.1 text. */
public class Card1261_083 extends AbstractNormalEffect {
    public Card1261_083() {
        super(Side.LIGHT, 3, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Mon Calamari Dockyards",
                Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_6_1, Rarity.V);
        setLore("Admiral Ackbar's hit-and-fade tactics force the Imperial Navy to spread throughout the galaxy in a futile attempt to engage the Rebels.");
        setGameText("Deploy on table. Star Cruisers (except Home One) may deploy -2 (to a maximum of -3), ignore deployment restrictions in their game text, draw one battle destiny if not able to otherwise, and are immune to attrition < 4. Capital Support is canceled. (Immune to Alter.)");
        addIcons(Icon.DEATH_STAR_II); addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        Filter cruisers = Filters.and(Filters.Star_Cruiser, Filters.not(Filters.Home_One));
        return Arrays.asList(new DeployCostModifier(self, cruisers, -2),
                new MaximumToReduceDeployCostByModifier(self, cruisers, 3),
                new IgnoresLocationDeploymentRestrictionsInGameTextModifier(self, cruisers),
                new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, cruisers, 1),
                new ImmuneToAttritionLessThanModifier(self, cruisers, 4));
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(SwccgGame game, Effect effect,
            PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.isPlayingCard(game, effect, Filters.title("Capital Support"))
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult,
            PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canTargetToCancel(game, self, Filters.title("Capital Support"))) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.title("Capital Support"), "Capital Support");
            return Collections.singletonList(action);
        }
        return null;
    }
}
