package com.gempukku.swccgo.cards.set15.light;
import com.gempukku.swccgo.cards.set12.light.Card12_059;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import java.util.List;

public class Card15_103 extends Card12_059 {
    public Card15_103() {
        setLore("");
        addIcon(Icon.CORUSCANT, 0);
        addIcons(Icon.REFLECTIONS_GOLD);
        addComboCardTitles("Mindful Of The Future", "Inconsequential Barriers");
        setGameText("Use 2 Force to deploy a unique (\u2022) battleground not on table, from Reserve Deck; reshuffle. OR Cancel Imperial Barrier, None Shall Pass, or Set For Stun. OR Peek at the top card of your Force Pile; either return it, or place it under your Force Pile or Reserve Deck.");
    }
    @Override public String getTitle() { return "Mindful Of The Future & Inconsequential Barriers"; }
    @Override public ExpansionSet getExpansionSet() { return ExpansionSet.REFLECTIONS_GOLD; }
    @Override public Rarity getRarity() { return Rarity.UNKNOWN; }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new java.util.ArrayList<>();
        List<PlayInterruptAction> barriers = super.getGameTextTopLevelActions(playerId, game, self);
        if (barriers != null) actions.addAll(barriers);
        GameTextActionId gameTextActionId = GameTextActionId.MINDFUL_OF_THE_FUTURE__DOWNLOAD_UNIQUE_BATTLEGROUND;

        // Check condition(s)
        if (GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 2)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.USED);
            action.setText("Deploy unique battleground from Reserve Deck");
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 2));
            // Allow response(s)
            action.allowResponses("Deploy a unique battleground not on table from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new DeployCardFromReserveDeckEffect(action, Filters.and(Filters.unique, Filters.location, Filters.not(Filters.sameTitleAs(self, Filters.onTable))), Filters.battleground, true));
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }
}
