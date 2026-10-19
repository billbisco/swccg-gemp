package com.gempukku.swccgo.cards.set1291.dark;

import com.gempukku.swccgo.cards.AbstractMission;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.evaluators.MaxLimitEvaluator;
import com.gempukku.swccgo.cards.evaluators.OnTableEvaluator;
import com.gempukku.swccgo.common.DestinyType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.PutStackedCardsInUsedPileEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.effects.choose.StackOneCardFromLostPileEffect;
import com.gempukku.swccgo.logic.modifiers.EachBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.ForceDrainInitiatedResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 9.1
 * Type: Mission
 * Title: Rally To Our Cause
 */
public class Card1291_028 extends AbstractMission {
    public Card1291_028() {
        super(Side.DARK, 5, PlayCardZoneOption.ATTACHED, "Rally To Our Cause", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_9_1, Rarity.V, false);
        setGameText("Deploy on War Has Begun. During your control phase, for each system where you occupy a related site, may draw one destiny; if destiny + X + Y > 7, stack top card of Lost Pile under that system (limit one per system); it is 'aligned' (Mission complete).\nX = number of 'aligned' systems on table.\nY = number of unique (•) [Separatist] characters on table.\n{Reward}: Your battle destiny draws are +X (limit +2).\n{Turn the Tide}: If opponent just initiated a Force drain at an 'aligned' system, place stacked cards there in Used Pile (system no longer 'aligned'). If there are no 'aligned' systems on table, place this Mission in Used Pile.");
        addIcons(Icon.EPISODE_I, Icon.SEPARATIST);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.title("War Has Begun");
    }

    @Override
    protected Filter getGameTextValidTargetFilterToRemainAttachedTo(final SwccgGame game, final PhysicalCard self) {
        return Filters.title("War Has Begun");
    }

    private Filter alignedSystems(final PhysicalCard self) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                if (!Filters.system.accepts(gameState, modifiersQuerying, physicalCard)) {
                    return false;
                }
                WhileInPlayData data = self.getWhileInPlayData();
                return data != null && data.getTextValues().contains(String.valueOf(physicalCard.getCardId()));
            }
        };
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new EachBattleDestinyModifier(self, Filters.any, new MaxLimitEvaluator(new OnTableEvaluator(self, alignedSystems(self)), 2), self.getOwner()));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        if (!GameConditions.isDuringYourPhase(game, playerId, Phase.CONTROL)
                || !GameConditions.canDrawDestiny(game, playerId)) {
            return null;
        }
        Filter eligible = new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                if (!Filters.system.accepts(gameState, modifiersQuerying, physicalCard)) {
                    return false;
                }
                if (alignedSystems(self).accepts(gameState, modifiersQuerying, physicalCard)) {
                    return false;
                }
                if (drawnThisPhase(self, physicalCard)) {
                    return false;
                }
                return GameConditions.occupies(game, playerId, Filters.relatedSite(physicalCard));
            }
        };
        if (!GameConditions.canTarget(game, self, eligible)) {
            return null;
        }
        final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId);
        action.setText("Draw destiny to align a system");
        action.appendTargeting(
                new TargetCardOnTableEffect(action, playerId, "Choose system to align", eligible) {
                    @Override
                    protected void cardTargeted(int targetGroupId, final PhysicalCard system) {
                        action.allowResponses("Draw destiny to align " + system.getTitle(),
                                new UnrespondableEffect(action) {
                                    @Override
                                    protected void performActionResults(Action targetingAction) {
                                        action.appendEffect(new PassthruEffect(action) {
                                            @Override
                                            protected void doPlayEffect(SwccgGame game) {
                                                markDrawnThisPhase(self, system);
                                            }
                                        });
                                        action.appendEffect(
                                                new DrawDestinyEffect(action, playerId, 1, DestinyType.DESTINY) {
                                                    @Override
                                                    protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                        GameState gameState = game.getGameState();
                                                        float draw = 0;
                                                        if (destinyDrawValues != null) {
                                                            for (Float value : destinyDrawValues) {
                                                                if (value != null) {
                                                                    draw += value;
                                                                }
                                                            }
                                                        }
                                                        float x = Filters.countActive(game, self, alignedSystems(self));
                                                        float y = Filters.countActive(game, self, Filters.and(Filters.unique, Filters.character, Filters.icon(Icon.SEPARATIST)));
                                                        if (draw + x + y <= 7) {
                                                            gameState.sendMessage("Result: Failed");
                                                            return;
                                                        }
                                                        PhysicalCard top = gameState.getTopOfLostPile(playerId);
                                                        if (top == null) {
                                                            gameState.sendMessage("Result: No card in Lost Pile");
                                                            return;
                                                        }
                                                        gameState.sendMessage("Result: Succeeded");
                                                        action.appendEffect(new StackOneCardFromLostPileEffect(action, top, system, false, false, false));
                                                        action.appendEffect(new PassthruEffect(action) {
                                                            @Override
                                                            protected void doPlayEffect(SwccgGame game) {
                                                                ensureData(self).getTextValues().add(String.valueOf(system.getCardId()));
                                                            }
                                                        });
                                                    }
                                                });
                                    }
                                });
                    }
                });
        return Collections.singletonList(action);
    }

    private WhileInPlayData ensureData(PhysicalCard self) {
        if (self.getWhileInPlayData() == null) {
            self.setWhileInPlayData(new WhileInPlayData());
        }
        return self.getWhileInPlayData();
    }

    private boolean drawnThisPhase(PhysicalCard self, PhysicalCard system) {
        WhileInPlayData data = self.getWhileInPlayData();
        return data != null && data.getPhysicalCards().contains(system);
    }

    private void markDrawnThisPhase(PhysicalCard self, PhysicalCard system) {
        List<PhysicalCard> drawn = ensureData(self).getPhysicalCards();
        if (!drawn.contains(system)) {
            drawn.add(system);
        }
    }

    private void clearDrawnThisPhase(PhysicalCard self) {
        WhileInPlayData data = self.getWhileInPlayData();
        if (data != null) {
            data.getPhysicalCards().clear();
        }
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.isStartOfYourPhase(game, self, effectResult, Phase.CONTROL)) {
            clearDrawnThisPhase(self);
        }
        String opponent = game.getOpponent(self.getOwner());
        Filter aligned = alignedSystems(self);
        if (TriggerConditions.forceDrainInitiatedBy(game, effectResult, opponent, aligned)) {
            PhysicalCard system = ((ForceDrainInitiatedResult) effectResult).getLocation();
            Collection<PhysicalCard> stacked = Filters.filterStacked(game, Filters.stackedOn(system));
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Place stacked cards in Used Pile");
            action.appendEffect(new PutStackedCardsInUsedPileEffect(action, opponent, stacked, true));
            action.appendEffect(new PassthruEffect(action) {
                @Override
                protected void doPlayEffect(SwccgGame game) {
                    if (self.getWhileInPlayData() != null) {
                        self.getWhileInPlayData().getTextValues().remove(String.valueOf(system.getCardId()));
                    }
                    if (self.getWhileInPlayData() == null || self.getWhileInPlayData().getTextValues().isEmpty()) {
                        action.appendEffect(new PlaceCardInUsedPileFromTableEffect(action, self));
                    }
                }
            });
            return Collections.singletonList(action);
        }
        return null;
    }
}
