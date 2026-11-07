package com.gempukku.swccgo.cards.set1250.light;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.Action;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/** Legacy Block 5 Artoo, Brave Little Droid, printed34. */
public class Card1250_036 extends AbstractDroid {
    public Card1250_036() {
        super(Side.LIGHT, 6, 3, 2, 4, "Artoo, Brave Little Droid", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_5, Rarity.V);
        setVirtualSuffix(true);
        setLore("Starship maintenance droid within the Naboo droid pool. Personally responsible for saving Amidala's starship and getting her to Tatooine.");
        setGameText("May fly (landspeed = 2). Once per game, may target up to two opponent's droids present; owner must place targets in Used Pile or lose 4 Force. While aboard an [Episode I] starship, it is power +3 and immune to attrition < 4 (or adds 2 to immunity).");
        addIcons(Icon.THEED_PALACE, Icon.EPISODE_I, Icon.NAV_COMPUTER);
        addModelType(ModelType.ASTROMECH); addPersona(Persona.R2D2);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        Filter ship = Filters.and(Filters.starship, Icon.EPISODE_I, Filters.hasAboard(self));
        Filter alreadyImmune = Filters.alreadyHasImmunityToAttrition(self);
        return List.of(new DefinedByGameTextLandspeedModifier(self, 2),
                new PowerModifier(self, ship, 3),
                new ImmuneToAttritionLessThanModifier(self, Filters.and(ship, Filters.not(alreadyImmune)), 4),
                new ImmunityToAttritionChangeModifier(self, Filters.and(ship, alreadyImmune), 2));
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game,
            PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId id = GameTextActionId.LEGACY__ARTOO_BRAVE_LITTLE_DROID_V__TARGET_DROIDS;
        Filter target = Filters.and(Filters.opponents(self), Filters.droid, Filters.presentWith(self));
        if (GameConditions.isOncePerGame(game, self, id) && GameConditions.canTarget(game, self, target)) {
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, id);
            action.setText("Target up to two opponent's droids");
            action.appendUsage(new OncePerGameEffect(action));
            action.appendTargeting(new TargetCardsOnTableEffect(action, playerId, "Choose up to two opponent's droids", 1, 2, target) {
                @Override
                protected void cardsTargeted(int targetGroupId, Collection<PhysicalCard> cards) {
                    action.addAnimationGroup(cards);
                    action.allowResponses("Target opponent's droids", new RespondableEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            Collection<PhysicalCard> targets = new ArrayList<>(action.getPrimaryTargetCards(targetGroupId));
                            if (targets.isEmpty()) return;
                            String opponent = game.getOpponent(playerId);
                            boolean canPlaceAll = targets.stream().noneMatch(card -> game.getModifiersQuerying()
                                    .mayNotBePlacedInUsedPile(game.getGameState(), card, opponent));
                            if (!canPlaceAll) {
                                action.appendEffect(new LoseForceEffect(action, opponent, 4, true));
                                return;
                            }
                            action.appendEffect(new PlayoutDecisionEffect(action, opponent,
                                    new MultipleChoiceAwaitingDecision("Place targeted droids in Used Pile or lose 4 Force",
                                            new String[]{"Place targets in Used Pile", "Lose 4 Force"}) {
                                        @Override
                                        protected void validDecisionMade(int index, String result) {
                                            if (index == 0) action.appendEffect(new PlaceCardsInUsedPileFromTableEffect(action, opponent, targets));
                                            else action.appendEffect(new LoseForceEffect(action, opponent, 4, true));
                                        }
                                    }));
                        }
                    });
                }
            });
            return Collections.singletonList(action);
        }
        return null;
    }
}
