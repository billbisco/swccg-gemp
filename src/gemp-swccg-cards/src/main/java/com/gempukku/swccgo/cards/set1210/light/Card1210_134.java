package com.gempukku.swccgo.cards.set1210.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.conditions.AttachedCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.DefenseValueModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.MinimumDeployCostToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.MovesForFreeModifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.AboutToForfeitCardFromTableResult;
import com.gempukku.swccgo.logic.timing.results.AboutToLeaveTableResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 1
 * Type: Effect
 * Title: Our Most Desperate Hour
 */
public class Card1210_134 extends AbstractNormalEffect {
    public Card1210_134() {
        super(Side.LIGHT, 6, PlayCardZoneOption.ATTACHED, "Our Most Desperate Hour", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_1, Rarity.V);
        setVirtualSuffix(true);
        setLore("Following her father Bail Organa's instructions, Leia placed the Death Star plans into R2-D2, hoping that they could be delivered safely to Alderaan by Obi-Wan Kenobi.");
        setGameText("Deploy on Leia of ability < 4. Leia moves for free, is power and defense value +2, draws one battle destiny if not able to otherwise, and, if she has spy skill, Rebels may deploy -1 here (to a minimum of 1). If Leia about to be lost, place her and this Effect in Used Pile instead. (Immune to Alter.)");
        addKeywords(Keyword.DEPLOYS_ON_CHARACTERS);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.Leia, Filters.abilityLessThan(4));
    }

    @Override
    protected Filter getGameTextValidTargetFilterToRemainAttachedToAfterCrossingOver(final SwccgGame game, final PhysicalCard self, PlayCardOptionId playCardOptionId) {
        return Filters.Leia;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter leia = Filters.hasAttached(self);
        Filter here = Filters.here(self);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MovesForFreeModifier(self, leia));
        modifiers.add(new PowerModifier(self, leia, 2));
        modifiers.add(new DefenseValueModifier(self, leia, 2));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, leia, 1));
        modifiers.add(new DeployCostToLocationModifier(self, Filters.Rebel, new AttachedCondition(self, Filters.spy), -1, here));
        modifiers.add(new MinimumDeployCostToTargetModifier(self, Filters.Rebel, new AttachedCondition(self, Filters.spy), 1, here));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        Filter leia = Filters.hasAttached(self);

        if (TriggerConditions.isAboutToBeForfeitedToLostPile(game, effectResult, leia)) {
            final AboutToForfeitCardFromTableResult result = (AboutToForfeitCardFromTableResult) effectResult;
            final PhysicalCard cardToBeLost = result.getCardAboutToLeaveTable();
            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Place Leia and this Effect in Used Pile");
            action.setActionMsg("Place " + GameUtils.getCardLink(cardToBeLost) + " and " + GameUtils.getCardLink(self) + " in Used Pile instead");
            action.appendEffect(
                    new PassthruEffect(action) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            result.getForfeitCardEffect().setForfeitToUsedPile();
                            action.appendEffect(
                                    new PlaceCardInUsedPileFromTableEffect(action, self, false, Zone.USED_PILE));
                        }
                    });
            return Collections.singletonList(action);
        }

        if (TriggerConditions.isAboutToBeLost(game, effectResult, leia)
                || TriggerConditions.isAboutToBeLost(game, effectResult, self)) {
            final AboutToLeaveTableResult result = (AboutToLeaveTableResult) effectResult;
            final PhysicalCard cardToBeLost = result.getCardAboutToLeaveTable();
            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Place Leia and this Effect in Used Pile");
            action.setActionMsg("Place " + GameUtils.getCardLink(cardToBeLost) + " and " + GameUtils.getCardLink(self) + " in Used Pile instead");
            action.appendEffect(
                    new PassthruEffect(action) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            result.getPreventableCardEffect().preventEffectOnCard(cardToBeLost);
                            action.appendEffect(
                                    new PlaceCardInUsedPileFromTableEffect(action, cardToBeLost, false, Zone.USED_PILE));
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}
