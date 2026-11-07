package com.gempukku.swccgo.cards.set1271.light;

import com.gempukku.swccgo.cards.AbstractUsedOrStartingInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.PutCardFromVoidInLostPileEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardsFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import java.util.Collections;
import java.util.List;

/** Virtual Block7.1: both printed modes; no original-printing battle-destiny mode. */
public class Card1271_044 extends AbstractUsedOrStartingInterrupt {
    public Card1271_044() {
        super(Side.LIGHT, 3, "Heading For The Medical Frigate", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_7_1, Rarity.V);
        setVirtualSuffix(true);
        setLore("At the Battle of Endor, Rebel starships fought to defend key capital starships, such as the Redemption.");
        setGameText("USED: Take one Redemption or a [Death Star II] capital starship (except a Star Cruiser) into hand from Reserve Deck; reshuffle. STARTING: Deploy from Reserve Deck up to three cards that have the [Setup] icon or are Effects that deploy for free, are always [Immune to Alter], and deploy on table. Place Interrupt in Lost Pile.");
        addIcons(Icon.DEATH_STAR_II);
    }
    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self) {
        GameTextActionId id = GameTextActionId.LEGACY__HEADING_FOR_THE_MEDICAL_FRIGATE__TAKE_STARSHIP;
        if (!GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, id)) return null;
        PlayInterruptAction action = new PlayInterruptAction(game, self, id, CardSubtype.USED);
        action.setText("Take Redemption or a qualifying capital starship into hand");
        action.allowResponses(new RespondablePlayCardEffect(action) {
            @Override protected void performActionResults(Action targetingAction) {
                action.appendEffect(new TakeCardIntoHandFromReserveDeckEffect(action, playerId,
                    Filters.and(Filters.canBeTargetedBy(self), Filters.or(Filters.Redemption,
                        Filters.and(Icon.DEATH_STAR_II, Filters.capital_starship, Filters.not(Filters.Star_Cruiser)))), true));
            }
        });
        return Collections.singletonList(action);
    }
    @Override
    protected PlayInterruptAction getGameTextStartingAction(String playerId, SwccgGame game, PhysicalCard self) {
        PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.STARTING);
        action.setText("Deploy up to three Setup cards or qualifying Effects");
        action.allowResponses(new RespondablePlayCardEffect(action) {
            @Override protected void performActionResults(Action targetingAction) {
                Filter eligible = Filters.and(Filters.canBeTargetedBy(self), Filters.or(Icon.SETUP,
                    Filters.and(Filters.Effect, Filters.deploysForFree, Filters.always_immune_to_Alter,
                        Filters.or(Filters.gameTextContains("deploy on table"), Filters.gameTextContains("deploy on your side of table")))));
                action.appendEffect(new DeployCardsFromReserveDeckEffect(action, eligible, 0, 3, false, false));
                action.appendEffect(new PutCardFromVoidInLostPileEffect(action, playerId, self));
            }
        });
        return action;
    }
}
