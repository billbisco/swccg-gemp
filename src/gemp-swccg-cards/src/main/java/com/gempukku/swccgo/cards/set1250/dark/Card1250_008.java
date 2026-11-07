package com.gempukku.swccgo.cards.set1250.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.AbstractPermanentWeapon;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.actions.FireWeaponActionBuilder;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.FireSingleWeaponAction;
import com.gempukku.swccgo.logic.actions.FireWeaponAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.RespondableWeaponFiringEffect;
import com.gempukku.swccgo.logic.effects.TargetCardsOnTableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.CancelImmunityToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotFireWeaponsModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.MoveCostFromLocationUsingLandspeedModifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;
import java.util.List;

/** Legacy Block 5: The Mandalorian. */
public class Card1250_008 extends AbstractAlien {
    public Card1250_008() {
        super(Side.DARK, 2, 4, 3, 3, 4, "The Mandalorian", Uniqueness.UNIQUE,
                ExpansionSet.VIRTUAL_BLOCK_5, Rarity.V);
        setArmor(5);
        setLore("Trade Federation.");
        setGameText("Adds 2 to power of anything he pilots. May deploy Jet Pack on him for free from Reserve Deck; reshuffle; he moves using it for -1 Force. Permanent weapon is Dual Blasters (may lose 1 Force to target a Jedi; until end of turn, choose: target may not fire weapons OR target loses immunity to attrition).");
        addIcons(Icon.EPISODE_I, Icon.PILOT, Icon.WARRIOR, Icon.PERMANENT_WEAPON, Icon.LEGACY_BLOCK_5);
        addKeyword(Keyword.TRADE_FEDERATION);
        setAsLegacy(true);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        return List.of(new AddsPowerToPilotedBySelfModifier(self, 2),
                new MoveCostFromLocationUsingLandspeedModifier(self, self, (state, querying) -> {
                    var jetPack = Filters.findFirstActive(state.getGame(), self,
                            Filters.and(Filters.Jet_Pack, Filters.attachedTo(self)));
                    return jetPack != null && GameConditions.canUseDevice(state.getGame(), jetPack);
                }, -1, Filters.any));
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game,
                                                                    PhysicalCard self, int sourceId) {
        var id = GameTextActionId.LEGACY__THE_MANDALORIAN__DEPLOY_JET_PACK;
        if (!GameConditions.canDeployCardFromReserveDeck(game, playerId, self, id, Title.Jet_Pack)) return null;
        var action = new TopLevelGameTextAction(self, sourceId, id);
        action.setText("Deploy Jet Pack on The Mandalorian");
        action.appendEffect(new DeployCardToTargetFromReserveDeckEffect(action, Filters.Jet_Pack,
                Filters.sameCardId(self), true, true));
        return List.of(action);
    }

    @Override
    protected AbstractPermanentWeapon getGameTextPermanentWeapon() {
        var blasters = new AbstractPermanentWeapon("Dual Blasters") {
            @Override
            public List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, SwccgGame game, PhysicalCard self,
                    boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring,
                    Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter,
                    boolean ignorePerAttackOrBattleLimit) {
                var builder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, this,
                        forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter,
                        fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                        .targetForFree(Filters.Jedi, TargetingReason.OTHER).finishBuildPrep();
                if (builder == null) return null;

                var action = new FireSingleWeaponAction(sourceCard, self, this, repeatedFiring, targetedAsCharacter,
                        defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit);
                action.setText("Fire Dual Blasters at a Jedi");
                action.appendTargeting(new TargetCardsOnTableEffect(action, playerId, "Choose a Jedi", 1, 1,
                        builder.getTargetFiltersMap(action.getCardFiringWeapon())) {
                    @Override
                    protected void cardsTargeted(int group, Collection<PhysicalCard> targets) {
                        action.addAnimationGroup(targets);
                        game.getGameState().getWeaponFiringState().setTargets(targets);
                        float force = builder.getUseForceCost(action.getCardFiringWeapon(), targets);
                        if (force > 0) action.appendCost(new UseForceEffect(action, playerId, force));
                        action.appendCost(new LoseForceEffect(action, playerId, 1));
                        action.allowResponses("Fire Dual Blasters", new RespondableWeaponFiringEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                var jedi = action.getPrimaryTargetCard(group);
                                game.getGameState().getWeaponFiringState().setTargets(List.of(jedi));
                                chooseBlasterResult(action, playerId, self, jedi);
                            }
                        });
                    }
                });
                return List.of(action);
            }
        };
        blasters.addKeyword(Keyword.BLASTER);
        return blasters;
    }

    private void chooseBlasterResult(Action action, String playerId, PhysicalCard self, PhysicalCard jedi) {
        var target = Filters.sameCardId(jedi);
        var options = new String[]{"Jedi may not fire weapons", "Jedi loses immunity to attrition"};
        action.appendEffect(new PlayoutDecisionEffect(action, playerId,
                new MultipleChoiceAwaitingDecision("Choose a Dual Blasters result", options) {
                    @Override
                    protected void validDecisionMade(int index, String result) {
                        Modifier modifier = index == 0 ? new MayNotFireWeaponsModifier(self, target)
                                : new CancelImmunityToAttritionModifier(self, target);
                        action.appendEffect(new AddUntilEndOfTurnModifierEffect(action, modifier, options[index]));
                    }
                }));
    }
}
