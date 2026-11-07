package com.gempukku.swccgo.cards.set1250.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.AbstractPermanentWeapon;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.HasAttachedCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.FireSingleWeaponAction;
import com.gempukku.swccgo.logic.actions.FireWeaponAction;
import com.gempukku.swccgo.logic.actions.FireWeaponActionBuilder;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.CancelImmunityToAttritionUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.RespondableWeaponFiringEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotFireWeaponsModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.MoveCostUsingLandspeedModifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 5
 * Type: Character
 * Subtype: Alien
 * Title: Jango Fett
 */
public class Card1250_008 extends AbstractAlien {
    public Card1250_008() {
        super(Side.DARK, 2, 4, 3, 3, 4, Title.Jango_Fett, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_5, Rarity.V);
        setArmor(5);
        setLore("Trade Federation.");
        setGameText("Adds 2 to power of anything he pilots. May deploy Jet Pack on him for free from Reserve Deck; reshuffle; he moves using it for -1 Force. Permanent weapon is Dual Blasters (may lose 1 Force to target a Jedi; until end of turn, choose: target may not fire weapons OR target loses immunity to attrition).");
        addIcons(Icon.PREMIUM, Icon.EPISODE_I, Icon.PILOT, Icon.WARRIOR, Icon.PERMANENT_WEAPON);
        addPersona(Persona.JANGO_FETT);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new MoveCostUsingLandspeedModifier(self, self, new HasAttachedCondition(self, Filters.Jet_Pack), -1));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__JANGO_FETT__DEPLOY_JET_PACK;
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, Title.Jet_Pack)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Jet Pack from Reserve Deck");
            action.setActionMsg("Deploy Jet Pack on " + GameUtils.getCardLink(self) + " for free from Reserve Deck");
            action.appendUsage(new OncePerTurnEffect(action));
            action.appendEffect(new DeployCardToTargetFromReserveDeckEffect(action, Filters.Jet_Pack, Filters.sameCardId(self), true, true));
            return Collections.singletonList(action);
        }
        return null;
    }

    // Define "Dual Blasters" permanent weapon
    @Override
    protected AbstractPermanentWeapon getGameTextPermanentWeapon() {
        return new AbstractPermanentWeapon("Dual Blasters") {
            @Override
            public List<FireWeaponAction> getGameTextFireWeaponActions(final String playerId, final SwccgGame game, final PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
                final FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, this, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                        .targetForFree(Filters.Jedi, TargetingReason.OTHER).finishBuildPrep();
                if (actionBuilder == null) {
                    return null;
                }
                final FireSingleWeaponAction action = new FireSingleWeaponAction(sourceCard, self, this, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit);
                action.setText("Fire " + action.getWeaponTitle(game));
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose Jedi to target", actionBuilder.getTargetFiltersMap(action.getCardFiringWeapon())) {
                            @Override
                            protected void cardTargeted(final int targetGroupId, PhysicalCard cardTargeted) {
                                action.addAnimationGroup(cardTargeted);
                                game.getGameState().getWeaponFiringState().setTarget(cardTargeted);
                                // Pay cost(s)
                                action.appendCost(new LoseForceEffect(action, playerId, 1, true));
                                // Allow response(s)
                                action.allowResponses("Fire " + GameUtils.getCardLink(action.getWeaponToFire()) + " at " + GameUtils.getCardLink(cardTargeted),
                                        new RespondableWeaponFiringEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                final PhysicalCard target = targetingAction.getPrimaryTargetCard(targetGroupId);
                                                game.getGameState().getWeaponFiringState().setTarget(target);
                                                action.appendEffect(
                                                        new PlayoutDecisionEffect(action, playerId,
                                                                new MultipleChoiceAwaitingDecision("Choose effect on " + GameUtils.getFullName(target),
                                                                        new String[]{"Target may not fire weapons", "Target loses immunity to attrition"}) {
                                                                    @Override
                                                                    protected void validDecisionMade(int index, String result) {
                                                                        if (index == 0) {
                                                                            action.appendEffect(new AddUntilEndOfTurnModifierEffect(action,
                                                                                    new MayNotFireWeaponsModifier(self, target),
                                                                                    GameUtils.getCardLink(target) + " may not fire weapons"));
                                                                        } else {
                                                                            action.appendEffect(new CancelImmunityToAttritionUntilEndOfTurnEffect(action, target,
                                                                                    GameUtils.getCardLink(target) + " loses immunity to attrition"));
                                                                        }
                                                                    }
                                                                }));
                                            }
                                        });
                            }
                        });
                return Collections.singletonList(action);
            }
        };
    }
}
