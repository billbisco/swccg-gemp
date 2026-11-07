package com.gempukku.swccgo.cards.set1240.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.DestinyModifier;
import com.gempukku.swccgo.logic.modifiers.LegacyMayPlayInterruptAsModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 4
 * Type: Effect
 * Title: Disarming Creature (V)
 */
public class Card1240_082 extends AbstractNormalEffect {
    public Card1240_082() {
        super(Side.LIGHT, 6, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Disarming_Creature, Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_BLOCK_4, Rarity.V);
        setVirtualSuffix(true);
        setLore("Luke's defensive maneuver put him out of arm's way.");
        setGameText("Deploy on table. Skull is destiny +2. Once per game, may deploy one Tauntaun Bones from Reserve Deck; reshuffle. You may play I Thought They Smelled Bad On The Outside as follows: 'USED: Lose 1 Force to take a creature (except Nudj), creature vehicle, or Beast-Rider into hand from Reserve Deck; reshuffle.' (Immune to Alter.)");
        addIcons(Icon.HOTH);
        addImmuneToCardTitle(Title.Alter);
    }

    // No Beast-Rider keyword exists in GEMP yet; match the printed name until one is added.
    private static final Filter BEAST_RIDER = Filters.or(Filters.titleContains("Beast-Rider"), Filters.titleContains("Beast Rider"));

    static Filter creatureCreatureVehicleOrBeastRider() {
        return Filters.or(Filters.and(Filters.creature, Filters.not(Filters.title("Nudj"))), Filters.creature_vehicle, BEAST_RIDER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DestinyModifier(self, Filters.title("Skull"), 2));
        modifiers.add(new LegacyMayPlayInterruptAsModifier(self, Filters.and(Filters.your(self), Filters.title("I Thought They Smelled Bad On The Outside")),
                CardSubtype.USED, new LegacyMayPlayInterruptAsModifier.PlayMode() {
            @Override
            public PlayInterruptAction createAction(final String playerId, SwccgGame game, PhysicalCard source, PhysicalCard interrupt) {
                GameTextActionId searchId = GameTextActionId.LEGACY__DISARMING_CREATURE_V__TAKE_CREATURE_WITH_I_THOUGHT_THEY_SMELLED_BAD;
                if (!GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, interrupt, searchId)) {
                    return null;
                }
                final PlayInterruptAction action = new PlayInterruptAction(game, interrupt, searchId);
                action.setText("Lose 1 Force to take card into hand from Reserve Deck");
                action.setActionMsg("Take a creature (except Nudj), creature vehicle, or Beast-Rider into hand from Reserve Deck");
                action.appendCost(
                        new LoseForceEffect(action, playerId, 1, true));
                action.allowResponses(
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                action.appendEffect(
                                        new TakeCardIntoHandFromReserveDeckEffect(action, playerId, creatureCreatureVehicleOrBeastRider(), true));
                            }
                        });
                return action;
            }
        }));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__DISARMING_CREATURE_V__DEPLOY_TAUNTAUN_BONES;
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Tauntaun Bones from Reserve Deck");
            action.appendUsage(
                    new OncePerGameEffect(action));
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.title("Tauntaun Bones"), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
