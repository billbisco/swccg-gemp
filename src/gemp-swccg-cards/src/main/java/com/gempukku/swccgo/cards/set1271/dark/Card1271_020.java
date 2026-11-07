package com.gempukku.swccgo.cards.set1271.dark;

import com.gempukku.swccgo.cards.AbstractLostOrStartingInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.LightSideGoesFirstEffect;
import com.gempukku.swccgo.logic.effects.PutCardFromVoidInLostPileEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardsFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import java.util.List;

/** Virtual Block 7.1 printing: Lost mode and all four Starting exclusions. */
public class Card1271_020 extends AbstractLostOrStartingInterrupt {
    public Card1271_020() {
        super(Side.DARK, 4, "According To My Design", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_7_1, Rarity.V);
        setLore("'Your fleet is lost. And your friends on the Endor moon will not survive. There is no escape, my young apprentice.'");
        setGameText("LOST: Use 3 Force to deploy Emperor from Reserve Deck; reshuffle. STARTING: Unless your 1st marker, Death Star, starship, or starship site on table, deploy from Reserve Deck Emperor to your site (even if converted), and deploy from Reserve Deck up to three Effects that deploy on table for free and are [Immune to Alter]. Light side goes first. Place Interrupt in Lost Pile.");
        addIcons(Icon.DEATH_STAR_II);
    }
    @Override protected List<PlayInterruptAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self) {
        GameTextActionId id = GameTextActionId.LEGACY__ACCORDING_TO_MY_DESIGN__DEPLOY_EMPEROR;
        if (!GameConditions.canUseForce(game, playerId, 3)
                || !GameConditions.canDeployCardFromReserveDeck(game, playerId, self, id)) return null;
        PlayInterruptAction action = new PlayInterruptAction(game, self, id, CardSubtype.LOST);
        action.setText("Deploy Emperor from Reserve Deck");
        action.appendCost(new UseForceEffect(action, playerId, 3));
        action.allowResponses(new RespondablePlayCardEffect(action) {
            @Override protected void performActionResults(Action targetingAction) {
                action.appendEffect(new DeployCardFromReserveDeckEffect(action, Filters.and(Filters.Emperor, Filters.canBeTargetedBy(self)), true));
            }
        });
        return List.of(action);
    }
    @Override protected PlayInterruptAction getGameTextStartingAction(String playerId, SwccgGame game, PhysicalCard self) {
        Filter yourSiteEvenIfConverted = Filters.and(Filters.site,
            Filters.or(Filters.your(self), Filters.convertedLocationOnTopOfLocation(Filters.your(self))));
        Filter forbidden = Filters.and(Filters.your(self), Filters.or(Filters.First_Marker,
            Filters.Death_Star_system, Filters.starship, Filters.starship_site));
        if (!Filters.filterAllOnTable(game, forbidden).isEmpty()
                || !GameConditions.canSpotLocation(game, yourSiteEvenIfConverted)) return null;
        PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.STARTING);
        action.setText("Deploy Emperor and up to three Effects from Reserve Deck");
        action.allowResponses(new RespondablePlayCardEffect(action) {
            @Override protected void performActionResults(Action targetingAction) {
                action.appendEffect(new DeployCardToLocationFromReserveDeckEffect(action,
                    Filters.and(Filters.Emperor, Filters.canBeTargetedBy(self)), yourSiteEvenIfConverted, true, false));
                Filter eligibleEffect = Filters.and(Filters.Effect, Filters.deploysForFree, Filters.immune_to_Alter, Filters.canBeTargetedBy(self),
                    Filters.or(Filters.gameTextContains("deploy on table"), Filters.gameTextContains("deploy on your side of table")));
                action.appendEffect(new DeployCardsFromReserveDeckEffect(action, eligibleEffect, 0, 3, true, false));
                action.appendEffect(new LightSideGoesFirstEffect(action));
                action.appendEffect(new PutCardFromVoidInLostPileEffect(action, playerId, self));
            }
        });
        return action;
    }
}
