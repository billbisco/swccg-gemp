package com.gempukku.swccgo.cards.set1210.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.UnlessCondition;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ForceDrainBonusesMayNotBeCanceledModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 1
 * Type: Character
 * Subtype: Alien
 * Title: Rodian
 */
public class Card1210_020 extends AbstractAlien {
    public Card1210_020() {
        super(Side.DARK, 3, 2, 2, 2, 4, "Rodian", Uniqueness.RESTRICTED_3, ExpansionSet.VIRTUAL_BLOCK_1, Rarity.V);
        setVirtualSuffix(true);
        setLore("Thuku is a typical male Rodian. Sent to hunt down Greedo by Navik the Red, head of the Chattza tribe. Rodians enjoy 'the hunt,' so many are employed as assassins.");
        setGameText("Once per game, may deploy a bounty hunter (except Greedo) to same site from Reserve Deck; reshuffle. Unless another •••Rodian is at same or related site, your Force drain bonuses at same planet site may not be canceled.");
        addIcons(Icon.PILOT, Icon.A_NEW_HOPE);
        setSpecies(Species.RODIAN);
        addKeyword(Keyword.ASSASSIN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        final int permCardId = self.getPermanentCardId();
        Condition anotherRodianAtSameOrRelatedSite = new Condition() {
            @Override
            public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
                PhysicalCard card = gameState.findCardByPermanentId(permCardId);
                return Filters.canSpotFromAllOnTable(gameState.getGame(),
                        Filters.and(Filters.not(card), Filters.title("Rodian"), Filters.atSameOrRelatedSite(card)));
            }
        };
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainBonusesMayNotBeCanceledModifier(self,
                new UnlessCondition(anotherRodianAtSameOrRelatedSite),
                Filters.your(playerId),
                Filters.and(Filters.planet_site, Filters.sameSite(self))));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__RODIAN_V__DEPLOY_BOUNTY_HUNTER;

        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.isAtLocation(game, self, Filters.site)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy bounty hunter from Reserve Deck");
            action.setActionMsg("Deploy a bounty hunter (except Greedo) to same site from Reserve Deck");
            action.appendUsage(
                    new OncePerGameEffect(action));
            action.appendEffect(
                    new DeployCardToLocationFromReserveDeckEffect(action, Filters.and(Filters.bounty_hunter, Filters.not(Filters.Greedo)), Filters.sameSite(self), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
