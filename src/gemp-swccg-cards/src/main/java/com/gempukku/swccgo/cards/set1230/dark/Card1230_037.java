package com.gempukku.swccgo.cards.set1230.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.HereCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.ExtraForceCostToDeployCardToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.MoveCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.SuspendsCardModifier;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 3
 * Type: Effect
 * Title: Security Precautions
 */
public class Card1230_037 extends AbstractNormalEffect {
    public Card1230_037() {
        super(Side.DARK, 5, PlayCardZoneOption.ATTACHED, Title.Security_Precautions, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_3, Rarity.V);
        setVirtualSuffix(true);
        setLore("'I think I got it.'");
        setGameText("Deploy on a site you occupy. Opponent's non-[Immune to Alter] Effects deployed on this site are suspended. While your weapon here, for opponent to deploy or move a character here requires +1 Force, and, during battle, may place your trooper here (unless alone) in Used Pile to cancel the firing of opponent's weapon here.");
        addIcons(Icon.ENDOR);
        addKeywords(Keyword.DEPLOYS_ON_SITE);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.site, Filters.occupies(self.getOwner()));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());
        Condition weaponHere = new HereCondition(self, Filters.and(Filters.your(self), Filters.weapon));
        Filter opponentsCharacters = Filters.and(Filters.opponents(self), Filters.character);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new SuspendsCardModifier(self, Filters.and(Filters.opponents(self), Filters.Effect, Filters.not(Filters.immune_to_Alter), Filters.attachedTo(Filters.here(self)))));
        modifiers.add(new ExtraForceCostToDeployCardToLocationModifier(self, opponentsCharacters, weaponHere, new com.gempukku.swccgo.logic.evaluators.ConstantEvaluator(1), Filters.here(self)));
        modifiers.add(new MoveCostToLocationModifier(self, opponentsCharacters, weaponHere, 1, Filters.here(self)));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalBeforeTriggers(final String playerId, SwccgGame game, final Effect effect, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerId);
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__SECURITY_PRECAUTIONS_V__CANCEL_FIRING;
        Filter trooperHere = Filters.and(Filters.your(self), Filters.trooper, Filters.here(self), Filters.not(Filters.alone));
        if (GameConditions.isDuringBattleAt(game, Filters.here(self))
                && TriggerConditions.isFiringWeapon(game, effect, opponent, Filters.here(self))
                && GameConditions.canSpot(game, self, trooperHere)
                && !effect.isCanceled()) {
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Place trooper in Used Pile to cancel firing");
            action.appendCost(
                    new ChooseCardOnTableEffect(action, playerId, "Choose trooper to place in Used Pile", trooperHere) {
                        @Override
                        protected void cardSelected(PhysicalCard selectedCard) {
                            action.appendCost(
                                    new PlaceCardInUsedPileFromTableEffect(action, selectedCard));
                        }
                    }
            );
            action.appendEffect(
                    new PassthruEffect(action) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            effect.cancel(self);
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}
