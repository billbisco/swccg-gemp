package com.gempukku.swccgo.cards.set15.dark;
import com.gempukku.swccgo.cards.set11.dark.Card11_075;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AloneAtCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.effects.choose.StealCardAndAttachFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ResetTotalAbilityModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Card15_213 extends Card11_075 {
    public Card15_213() {
        setLore(""); setGameText("Deploy on table if all of your race totals = 0. When a player wins a Podrace, adds 5 to the amount retrieved. When a player loses a Podrace, adds 5 to the amount lost. Also, while Aurra Sing is alone at a site, your total ability at that site = 0. During your move phase, may lose Effect to target an opponent's lightsaber present with Aurra Sing. Draw destiny. If destiny > 3, Aurra Sing may 'steal' that lightsaber. (Immune to Alter.)");
        addComboCardTitles(Title.Wattos_Box,Title.Quietly_Observing); addIcon(Icon.TATOOINE,0); addIcons(Icon.REFLECTIONS_GOLD);
    }
    @Override public String getTitle() { return "Watto's Box & Quietly Observing"; }
    @Override public Float getDestiny() { return 3f; }
    @Override public ExpansionSet getExpansionSet() { return ExpansionSet.REFLECTIONS_GOLD; }
    @Override public Rarity getRarity() { return Rarity.UNKNOWN; }
    @Override protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game,PhysicalCard self) {
        List<Modifier> modifiers=new LinkedList<>(super.getGameTextWhileActiveInPlayModifiers(game,self));
        modifiers.add(new ResetTotalAbilityModifier(self,Filters.and(Filters.site,Filters.sameSiteAs(self,Filters.and(Filters.Aurra,Filters.alone))),0,self.getOwner()));
        return modifiers;
    }
    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        final PhysicalCard aurra = Filters.findFirstActive(game,self,Filters.Aurra);
        if(aurra==null) return null;
        Filter targetFilter = Filters.and(Filters.opponents(self), Filters.lightsaber, Filters.present(aurra), Filters.canBeTargetedBy(aurra, TargetingReason.TO_BE_STOLEN));
        TargetingReason targetingReason = TargetingReason.TO_BE_STOLEN;

        // Check condition(s)
        if (GameConditions.isDuringYourPhase(game, self, Phase.MOVE)
                && GameConditions.canTarget(game, self, targetingReason, targetFilter)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("'Steal' lightsaber");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Target lightsaber", targetingReason, targetFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, final PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Pay cost(s)
                            action.appendCost(
                                    new LoseCardFromTableEffect(action, self));
                            // Allow response(s)
                            action.allowResponses("'Steal' " + GameUtils.getCardLink(targetedCard),
                                    new UnrespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(final Action targetingAction) {
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new DrawDestinyEffect(action, playerId) {
                                                        @Override
                                                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, final Float totalDestiny) {
                                                            final GameState gameState = game.getGameState();
                                                            if (totalDestiny == null) {
                                                                gameState.sendMessage("Result: Failed due to failed destiny draw");
                                                                return;
                                                            }

                                                            gameState.sendMessage("Destiny: " + GuiUtils.formatAsString(totalDestiny));
                                                            if (totalDestiny > 3) {
                                                                gameState.sendMessage("Result: Succeeded");
                                                                action.appendEffect(
                                                                        new PlayoutDecisionEffect(action, playerId,
                                                                                new YesNoDecision("Do you want to 'steal' " + GameUtils.getCardLink(targetedCard) + "?") {
                                                                                    @Override
                                                                                    protected void yes() {
                                                                                        gameState.sendMessage(playerId + " chooses to 'steal' " + GameUtils.getCardLink(targetedCard));
                                                                                        action.appendEffect(
                                                                                                new StealCardAndAttachFromTableEffect(action, targetedCard, aurra));
                                                                                    }
                                                                                    @Override
                                                                                    protected void no() {
                                                                                        gameState.sendMessage(playerId + " chooses to not 'steal' " + GameUtils.getCardLink(targetedCard));
                                                                                    }
                                                                                }));
                                                            }
                                                            else {
                                                                gameState.sendMessage("Result: Failed");
                                                            }
                                                        }
                                                    });
                                        }
                                    });
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}
