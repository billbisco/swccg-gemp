package com.gempukku.swccgo.cards.set1262.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.CancelCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotBeFlippedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 6.2
 * Type: Effect
 * Title: Altering The Deal
 *
 * The "Dark Deal may deploy here if you control three Bespin sites" clause is implemented on Dark Deal (Card5_115).
 */
public class Card1262_003 extends AbstractNormalEffect {
    public Card1262_003() {
        super(Side.DARK, 3, PlayCardZoneOption.ATTACHED, Title.Altering_The_Deal, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_6_2, Rarity.V);
        setLore("Gamblers are vulnerable to bribery, extortion and other forms of manipulation. Their notorious dealings can be easily taken advantage of.");
        setGameText("Deploy on Dining Room. Dark Deal may deploy here if you control three Bespin sites, and is canceled if opponent controls this site. While Dark Deal on table and you occupy Bespin, This Deal Is Getting Worse All The Time flips and may not flip back. (Immune to Alter.)");
        addIcons(Icon.CLOUD_CITY);
        addKeywords(Keyword.DEPLOYS_ON_LOCATION);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Dining_Room;
    }

    private static boolean dealConditions(SwccgGame game, PhysicalCard self) {
        return GameConditions.canSpot(game, self, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.Dark_Deal)
                && GameConditions.occupies(game, self.getOwner(), SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.Bespin_system);
    }

    private static Filter yourThisDeal(PhysicalCard self, String title) {
        return Filters.and(Filters.owner(self.getOwner()), Filters.Objective, Filters.title(title));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(final SwccgGame game, final PhysicalCard self) {
        Condition dealCondition = new Condition() {
            @Override
            public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
                return dealConditions(gameState.getGame(), self);
            }
        };
        List<Modifier> modifiers = new LinkedList<Modifier>();
        // Flipped side of This Deal Is Getting Worse All The Time may not flip back
        modifiers.add(new MayNotBeFlippedModifier(self, dealCondition, yourThisDeal(self, Title.Pray_I_Dont_Alter_It_Any_Further)));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();
        if (!TriggerConditions.isTableChanged(game, effectResult)) {
            return null;
        }
        String opponent = game.getOpponent(self.getOwner());
        PhysicalCard diningRoom = self.getAttachedTo();

        // Dark Deal (here) is canceled if opponent controls this site
        if (diningRoom != null && GameConditions.controls(game, opponent, diningRoom)) {
            final PhysicalCard darkDeal = Filters.findFirstActive(game, self, Filters.and(Filters.Dark_Deal, Filters.attachedTo(diningRoom)));
            if (darkDeal != null && GameConditions.canBeCanceled(game, darkDeal)) {
                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setSingletonTrigger(true);
                action.setText("Cancel Dark Deal");
                action.setActionMsg("Cancel " + GameUtils.getCardLink(darkDeal) + ", since opponent controls " + GameUtils.getCardLink(diningRoom));
                action.appendEffect(new CancelCardOnTableEffect(action, darkDeal));
                actions.add(action);
            }
        }

        // This Deal Is Getting Worse All The Time flips
        if (dealConditions(game, self)) {
            final PhysicalCard objective = Filters.findFirstActive(game, self, yourThisDeal(self, Title.This_Deal_Is_Getting_Worse_All_The_Time));
            if (objective != null && GameConditions.canBeFlipped(game, objective)) {
                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setSingletonTrigger(true);
                action.setText("Flip " + GameUtils.getFullName(objective));
                action.setActionMsg("Flip " + GameUtils.getCardLink(objective));
                action.appendEffect(new FlipCardEffect(action, objective));
                actions.add(action);
            }
        }
        return actions;
    }
}
