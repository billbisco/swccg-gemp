package com.gempukku.swccgo.cards.set1261.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.ConvertLocationByRaisingToTopEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotDrawMoreThanBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.Action;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/** Underworld Contacts (V), Virtual Block 6.1. */
public class Card1261_089 extends AbstractNormalEffect {
    public Card1261_089() {
        super(Side.LIGHT, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Underworld_Contacts, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_6_1, Rarity.V);
        setVirtualSuffix(true);
        setLore("Influence, like any commodity, has a price in the Outer Rim territories.");
        setGameText("Deploy on table. While any player has two aliens of the same card title together, they draw one battle destiny if unable to otherwise, and players draw no more than one battle destiny there. Once per turn, may raise your converted Jabba's Palace site to the top. (Immune to Alter.)");
        addIcons(Icon.PREMIUM);
        addImmuneToCardTitle(Title.Alter);
    }

    private Filter alienInPair(PhysicalCard source) {
        return new Filter() {
            @Override
            public boolean accepts(GameState state, ModifiersQuerying query, PhysicalCard card) {
                return Filters.alien.accepts(state, query, card)
                        && GameConditions.canSpot(state.getGame(), source, Filters.and(Filters.your(card.getOwner()),
                                Filters.alien, Filters.sameTitle(card), Filters.with(card)));
            }
        };
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        Filter pairMember = alienInPair(self);
        Filter pairLocation = new Filter() {
            @Override
            public boolean accepts(GameState state, ModifiersQuerying query, PhysicalCard location) {
                return GameConditions.canSpot(state.getGame(), self, Filters.and(pairMember, Filters.at(location)));
            }
        };
        return Arrays.asList(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, pairMember, 1),
                new MayNotDrawMoreThanBattleDestinyModifier(self, pairLocation, 1, game.getLightPlayer()),
                new MayNotDrawMoreThanBattleDestinyModifier(self, pairLocation, 1, game.getDarkPlayer()));
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        Filter site = Filters.and(Filters.Jabbas_Palace_site, Filters.canBeConvertedByRaisingYourLocationToTop(playerId));
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId) && GameConditions.canTarget(game, self, site)) {
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Raise a converted Jabba's Palace site");
            action.appendUsage(new OncePerTurnEffect(action));
            action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Choose Jabba's Palace site to raise", site) {
                @Override
                protected void cardTargeted(int targetGroupId, PhysicalCard targetedCard) {
                    action.addAnimationGroup(targetedCard);
                    action.allowResponses("Raise " + GameUtils.getCardLink(targetedCard), new UnrespondableEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(new ConvertLocationByRaisingToTopEffect(action, action.getPrimaryTargetCard(targetGroupId), true));
                        }
                    });
                }
            });
            return Collections.singletonList(action);
        }
        return null;
    }
}
