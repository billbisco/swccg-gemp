package com.gempukku.swccgo.cards.set1272.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.cards.evaluators.StackedEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.DefenseValueModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.HitResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 7.2
 * Type: Effect
 * Title: Trophy Of A Bounty Hunter
 */
public class Card1272_006 extends AbstractNormalEffect {
    public Card1272_006() {
        super(Side.DARK, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Trophy Of A Bounty Hunter", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_7_2, Rarity.V);
        setLore("The Abyssins are known for their regenerative abilities. Jabba keeps several statue heads of Abyssin creatures on his repulsor sled.");
        setGameText("Deploy on table. Whenever opponent's card hit by your bounty hunter leaves table, stack it here face down. Your bounty hunters are power and defense value +X, where X = number of cards stacked here. Once per game, may deploy Desilijic Tattoo or Jabba's Influence from Reserve Deck; reshuffle. (Immune to Alter.)");
        addImmuneToCardTitle(Title.Alter);
    }

    /** Opponent's cards hit by your bounty hunters (while this card was on table) that have not yet left table. */
    private static List<PhysicalCard> hitByBountyHunters(PhysicalCard self) {
        WhileInPlayData data = self.getWhileInPlayData();
        if (data == null || data.getPhysicalCards() == null)
            return new ArrayList<PhysicalCard>();
        return new ArrayList<PhysicalCard>(data.getPhysicalCards());
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter yourBountyHunters = Filters.and(Filters.your(self), Filters.bounty_hunter);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, yourBountyHunters, new StackedEvaluator(self)));
        modifiers.add(new DefenseValueModifier(self, yourBountyHunters, new StackedEvaluator(self)));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Remember opponent's cards hit by your bounty hunter
        if (TriggerConditions.justHitBy(game, effectResult, Filters.opponents(self), Filters.and(Filters.your(self), Filters.bounty_hunter))) {
            final PhysicalCard cardHit = ((HitResult) effectResult).getCardHit();
            List<PhysicalCard> tracked = hitByBountyHunters(self);
            if (cardHit != null && !tracked.contains(cardHit)) {
                tracked.add(cardHit);
                self.setWhileInPlayData(new WhileInPlayData(tracked));
            }
            return null;
        }

        final List<PhysicalCard> tracked = hitByBountyHunters(self);
        if (tracked.isEmpty())
            return null;

        // Whenever such a card leaves table, stack it here face down
        if (TriggerConditions.leavesTable(game, effectResult, Filters.in(tracked))) {
            final List<PhysicalCard> leftTable = new ArrayList<PhysicalCard>();
            for (PhysicalCard card : tracked) {
                if (card.getZone() == null || !card.getZone().isInPlay()) {
                    leftTable.add(card);
                }
            }
            if (leftTable.isEmpty())
                return null;

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Stack " + GameUtils.getAppendedNames(leftTable) + " here face down");
            action.setActionMsg("Stack " + GameUtils.getAppendedNames(leftTable) + " on " + GameUtils.getCardLink(self) + " face down");
            // Perform result(s)
            action.appendEffect(
                    new PassthruEffect(action) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            List<PhysicalCard> remaining = hitByBountyHunters(self);
                            remaining.removeAll(leftTable);
                            self.setWhileInPlayData(remaining.isEmpty() ? null : new WhileInPlayData(remaining));
                            for (PhysicalCard card : leftTable) {
                                if (card.getZone() != null && !card.getZone().isInPlay() && card.getStackedOn() != self) {
                                    game.getGameState().removeCardsFromZone(Collections.singleton(card));
                                    game.getGameState().stackCard(card, self, true, false, false);
                                }
                            }
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__TROPHY_OF_A_BOUNTY_HUNTER__DEPLOY_TATTOO_OR_INFLUENCE;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Desilijic Tattoo or Jabba's Influence from Reserve Deck");
            action.setActionMsg("Deploy Desilijic Tattoo or Jabba's Influence from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.or(Filters.title("Desilijic Tattoo"), Filters.Jabbas_Influence), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
