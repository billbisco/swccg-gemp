package com.gempukku.swccgo.cards.set1109.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.ForfeitCardFromTableUsingForfeitValueEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ForfeitedToUsedPileModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.PlayCardResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 9
 * Type: Effect
 * Title: Superficial Damage
 */
public class Card1109_019 extends AbstractNormalEffect {
    public Card1109_019() {
        super(Side.LIGHT, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Superficial Damage", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_9, Rarity.V);
        setVirtualSuffix(true);
        setLore("Veteran Rebel engineers know that weapon systems positioned for adequate coverage are vulnerable themselves.");
        setGameText("Deploy on table. You may forfeit non-lightsaber weapons (using forfeit value = 3); place your just forfeited weapon in Used Pile. During your deploy phase, if you just deployed a vehicle or starship, may deploy a weapon (for free) on that vehicle or starship from Reserve Deck; reshuffle. (Immune to Alter.)");
        addIcons(Icon.DEATH_STAR_II);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForfeitedToUsedPileModifier(self, Filters.and(Filters.your(self), Filters.weapon)));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.isStartOfEachTurn(game, effectResult)) {
            self.setWhileInPlayData(null);
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.isResolvingBattleDamageAndAttrition(game, effectResult, playerId)
                && (GameConditions.isBattleDamageRemaining(game, playerId) || GameConditions.isAttritionRemaining(game, playerId))) {
            Collection<PhysicalCard> cardsWithAttachedWeaponToForfeit = Filters.filterActive(game, self,
                    Filters.and(Filters.your(self), Filters.or(Filters.character, Filters.starship, Filters.vehicle), Filters.participatingInBattle,
                            Filters.hasAttached(Filters.and(Filters.your(self), Filters.weapon, Filters.except(Filters.lightsaber)))));
            if (!cardsWithAttachedWeaponToForfeit.isEmpty()) {
                List<PhysicalCard> cardsToAlreadyForfeitWeapon = self.getWhileInPlayData() != null ? self.getWhileInPlayData().getPhysicalCards() : null;
                if (cardsToAlreadyForfeitWeapon != null) {
                    cardsWithAttachedWeaponToForfeit = Filters.filter(cardsWithAttachedWeaponToForfeit, game, Filters.not(Filters.in(cardsToAlreadyForfeitWeapon)));
                }
                if (!cardsWithAttachedWeaponToForfeit.isEmpty()) {
                    Collection<PhysicalCard> weaponsToForfeit = Filters.filterActive(game, self,
                            Filters.and(Filters.your(self), Filters.weapon, Filters.except(Filters.lightsaber), Filters.attachedTo(Filters.in(cardsWithAttachedWeaponToForfeit))));
                    if (!weaponsToForfeit.isEmpty()) {

                        final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                        action.setText("Forfeit a weapon");
                        action.appendTargeting(
                                new ChooseCardOnTableEffect(action, playerId, "Choose weapon to forfeit", weaponsToForfeit) {
                                    @Override
                                    protected void cardSelected(PhysicalCard selectedCard) {
                                        action.addAnimationGroup(selectedCard);
                                        action.setActionMsg("Forfeit " + GameUtils.getCardLink(selectedCard));
                                        PhysicalCard attachedTo = selectedCard.getAttachedTo();
                                        if (attachedTo != null) {
                                            List<PhysicalCard> cardsToAlreadyForfeitWeapon = self.getWhileInPlayData() != null ? self.getWhileInPlayData().getPhysicalCards() : null;
                                            if (cardsToAlreadyForfeitWeapon == null) {
                                                self.setWhileInPlayData(new WhileInPlayData(new ArrayList<PhysicalCard>()));
                                                cardsToAlreadyForfeitWeapon = self.getWhileInPlayData().getPhysicalCards();
                                            }
                                            cardsToAlreadyForfeitWeapon.add(attachedTo);
                                        }
                                        action.appendEffect(
                                                new ForfeitCardFromTableUsingForfeitValueEffect(action, selectedCard, 3));
                                    }
                                }
                        );
                        return Collections.singletonList(action);
                    }
                }
            }
        }

        GameTextActionId gameTextActionId = GameTextActionId.OVS__SUPERFICIAL_DAMAGE__DEPLOY_WEAPON;
        if (GameConditions.isDuringYourPhase(game, playerId, Phase.DEPLOY)
                && TriggerConditions.justDeployed(game, effectResult, playerId, Filters.and(Filters.your(self), Filters.or(Filters.vehicle, Filters.starship)))
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {
            final PhysicalCard justDeployed = ((PlayCardResult) effectResult).getPlayedCard();
            if (justDeployed != null) {
                final OptionalGameTextTriggerAction deployWeapon = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                deployWeapon.setText("Deploy a weapon for free");
                deployWeapon.setActionMsg("Deploy a weapon for free on " + GameUtils.getCardLink(justDeployed) + " from Reserve Deck");
                deployWeapon.appendEffect(
                        new DeployCardToTargetFromReserveDeckEffect(deployWeapon, Filters.weapon, Filters.sameCardId(justDeployed), true, true));
                return Collections.singletonList(deployWeapon);
            }
        }
        return null;
    }
}
