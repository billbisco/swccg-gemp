package com.gempukku.swccgo.cards.set1281.dark;

import com.gempukku.swccgo.cards.AbstractUtinniEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetId;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.UtinniEffectStatus;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.RelocateCardToSideOfTableEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotBeCanceledModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 8.1
 * Type: Effect
 * Subtype: Utinni
 * Title: Death Mark & Hutt Bounty
 */
public class Card1281_010 extends AbstractUtinniEffect {
    public Card1281_010() {
        super(Side.DARK, 5, PlayCardZoneOption.ATTACHED, "Death Mark & Hutt Bounty", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_8_1, Rarity.V);
        addComboCardTitles(Title.Death_Mark, Title.Hutt_Bounty);
        setLore("'A death mark's not an easy thing to live with. You're a good fighter, Solo. I hate to lose you.'");
        setGameText("If On The Hunt on table, deploy on a 'mark' (opponent's non-droid character at a battleground site). Target an assassin at another site. During any move phase, when reached by target, draw destiny and add target's power; opponent draws two destiny (choosing one) and adds 'mark's defense value. If your total is higher, 'mark' is 'assassinated': opponent loses 2 Force (4 if a Jedi), 'mark' is lost, and relocate Death Mark to your side of table. Otherwise, place Death Mark (and may place assassin it targets) in Used Pile. May not be canceled. (Immune to Alter.)");
        addKeywords(Keyword.DEPLOYS_ON_CHARACTERS);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return GameConditions.canSpot(game, self, Filters.title("On The Hunt"));
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.opponents(self), Filters.non_droid_character, Filters.at(Filters.battleground_site));
    }

    @Override
    protected Filter getGameTextValidUtinniEffectTargetFilter(String playerId, SwccgGame game, PhysicalCard self, PhysicalCard deployTarget, TargetId targetId) {
        Filter filter = Filters.and(Filters.assassin, Filters.at(Filters.site));
        if (deployTarget != null) {
            filter = Filters.and(filter, Filters.not(Filters.atSameLocation(deployTarget)));
        }
        return filter;
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotBeCanceledModifier(self, self));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        final GameState gameState = game.getGameState();
        final PhysicalCard assassin = self.getTargetedCard(gameState, TargetId.UTINNI_EFFECT_TARGET_1);
        final PhysicalCard mark = self.getAttachedTo();

        // Check condition(s)
        if (!GameConditions.isUtinniEffectReached(game, self)
                && TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.isDuringEitherPlayersPhase(game, Phase.MOVE)
                && assassin != null
                && mark != null
                && GameConditions.isAtLocation(game, self, Filters.sameLocation(assassin))) {

            final String playerId = self.getOwner();
            final String opponent = game.getOpponent(playerId);
            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setSingletonTrigger(true);
            action.setText("Attempt to 'assassinate' " + GameUtils.getFullName(mark));
            action.setActionMsg("Attempt to 'assassinate' " + GameUtils.getCardLink(mark) + " with " + GameUtils.getCardLink(assassin));
            action.appendUsage(
                    new PassthruEffect(action) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            self.setUtinniEffectStatus(UtinniEffectStatus.REACHED);
                        }
                    });
            // Perform result(s)
            action.appendEffect(
                    new DrawDestinyEffect(action, playerId) {
                        @Override
                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                            final float yourTotal = (totalDestiny != null ? totalDestiny : 0) + game.getModifiersQuerying().getPower(game.getGameState(), assassin);
                            game.getGameState().sendMessage(playerId + "'s total: " + GuiUtils.formatAsString(yourTotal));
                            action.appendEffect(
                                    new DrawDestinyEffect(action, opponent, 2, 1) {
                                        @Override
                                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                            GameState gameState = game.getGameState();
                                            float opponentsTotal = (totalDestiny != null ? totalDestiny : 0) + game.getModifiersQuerying().getDefenseValue(gameState, mark);
                                            gameState.sendMessage(opponent + "'s total: " + GuiUtils.formatAsString(opponentsTotal));
                                            if (yourTotal > opponentsTotal) {
                                                gameState.sendMessage("Result: " + GameUtils.getCardLink(mark) + " is 'assassinated'");
                                                action.appendEffect(
                                                        new LoseForceEffect(action, opponent, Filters.Jedi.accepts(game, mark) ? 4 : 2));
                                                action.appendEffect(
                                                        new RelocateCardToSideOfTableEffect(action, self, playerId));
                                                action.appendEffect(
                                                        new LoseCardFromTableEffect(action, mark));
                                            }
                                            else {
                                                gameState.sendMessage("Result: 'Assassination' failed");
                                                action.appendEffect(
                                                        new PlaceCardInUsedPileFromTableEffect(action, self));
                                                action.appendEffect(
                                                        new PlayoutDecisionEffect(action, playerId,
                                                                new YesNoDecision("Place " + GameUtils.getCardLink(assassin) + " in Used Pile?") {
                                                                    @Override
                                                                    protected void yes() {
                                                                        action.appendEffect(
                                                                                new PlaceCardInUsedPileFromTableEffect(action, assassin));
                                                                    }
                                                                }));
                                            }
                                        }
                                    });
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}
