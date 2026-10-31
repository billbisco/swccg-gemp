package com.gempukku.swccgo.cards.set1109.dark;

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
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.ForfeitCardFromTableUsingForfeitValueEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ForfeitedToUsedPileModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.PlayCardResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 9
 * Type: Effect
 * Title: Inconsequential Losses
 */
public class Card1109_039 extends AbstractNormalEffect {
    public Card1109_039() {
        super(Side.DARK, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Inconsequential_Losses, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_9, Rarity.V);
        setVirtualSuffix(true);
        setLore("With its superior numbers, the Empire knowingly sacrifices the safety of many units and weapons in order to preserve the greater war machine.");
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
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<OptionalGameTextTriggerAction> actions = new LinkedList<OptionalGameTextTriggerAction>();

        if (TriggerConditions.isResolvingBattleDamageAndAttrition(game, effectResult, playerId)
                && (GameConditions.isBattleDamageRemaining(game, playerId) || GameConditions.isAttritionRemaining(game, playerId))) {
            Collection<PhysicalCard> weaponsToForfeit = Filters.filterActive(game, self,
                    Filters.and(Filters.your(self), Filters.weapon, Filters.except(Filters.lightsaber), Filters.participatingInBattle));
            if (!weaponsToForfeit.isEmpty()) {
                final OptionalGameTextTriggerAction forfeit = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                forfeit.setText("Forfeit a weapon");
                forfeit.appendTargeting(
                        new ChooseCardOnTableEffect(forfeit, playerId, "Choose weapon to forfeit", weaponsToForfeit) {
                            @Override
                            protected void cardSelected(PhysicalCard selectedCard) {
                                forfeit.addAnimationGroup(selectedCard);
                                forfeit.setActionMsg("Forfeit " + GameUtils.getCardLink(selectedCard));
                                forfeit.appendEffect(
                                        new ForfeitCardFromTableUsingForfeitValueEffect(forfeit, selectedCard, 3));
                            }
                        }
                );
                actions.add(forfeit);
            }
        }

        GameTextActionId deployId = GameTextActionId.LEGACY__INCONSEQUENTIAL_LOSSES_V__DEPLOY_WEAPON;
        if (TriggerConditions.justDeployed(game, effectResult, Filters.and(Filters.your(self), Filters.or(Filters.vehicle, Filters.starship)))
                && GameConditions.isDuringYourPhase(game, playerId, Phase.DEPLOY)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, deployId)
                && effectResult instanceof PlayCardResult) {
            final PhysicalCard deployed = ((PlayCardResult) effectResult).getPlayedCard();
            if (deployed != null) {
                final OptionalGameTextTriggerAction deployWeapon = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId, deployId);
                deployWeapon.setText("Deploy a weapon on " + GameUtils.getFullName(deployed));
                deployWeapon.setActionMsg("Deploy a weapon for free on " + GameUtils.getCardLink(deployed) + " from Reserve Deck");
                deployWeapon.appendEffect(
                        new DeployCardToTargetFromReserveDeckEffect(deployWeapon, Filters.weapon, Filters.sameCardId(deployed), true, true));
                actions.add(deployWeapon);
            }
        }

        return actions.isEmpty() ? null : actions;
    }
}
