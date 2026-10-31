package com.gempukku.swccgo.cards.set1112.dark;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
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
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardsInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.PutCardFromLostPileInUsedPileEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostForSimultaneouslyDeployingPilotModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.AboutToLeaveTableResult;
import com.gempukku.swccgo.logic.timing.results.LostFromTableResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 12: Martyrs & Magistrates
 * Type: Starship
 * Subtype: Starfighter
 * Title: Obsidian 10
 */
public class Card1112_039 extends AbstractStarfighter {
    public Card1112_039() {
        super(Side.DARK, 3, 1, 1, null, 3, null, 3, Title.Obsidian_10, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_12, Rarity.V);
        setVirtualSuffix(true);
        setLore("Recalled to defend second Death Star during construction. Stationed aboard Thunderflare.");
        setGameText("Heroic. May add 1 pilot. OS-72-10 deploys -2 aboard. When lost from Endor, a mobile system, or a sector, may place this starship (and cards aboard it) in owner's Used Pile.");
        addIcons(Icon.DEATH_STAR_II);
        addKeywords(Keyword.OBSIDIAN_SQUADRON, Keyword.NO_HYPERDRIVE, Keyword.HEROIC);
        addModelType(ModelType.TIE_LN);
        setPilotCapacity(1);
        setMatchingPilotFilter(Filters.OS_72_10);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostForSimultaneouslyDeployingPilotModifier(self, Filters.OS_72_10, -2));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiersEvenIfUnpiloted(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToTargetModifier(self, Filters.OS_72_10, -2, self));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        return getPlaceInUsedPileAction(game, effectResult, self, gameTextSourceCardId);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggersWhenInactiveInPlay(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        return getPlaceInUsedPileAction(game, effectResult, self, gameTextSourceCardId);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextLeavesTableOptionalTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__OBSIDIAN_10_V__PLACE_IN_USED_PILE;
        if (TriggerConditions.justLostFromLocation(game, effectResult, self,
                Filters.or(Filters.Endor_location, Filters.Endor_system, Filters.mobile_system, Filters.sector))) {
            LostFromTableResult lost = (LostFromTableResult) effectResult;
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Place " + GameUtils.getFullName(self) + " in Used Pile");
            action.setActionMsg("Place " + GameUtils.getCardLink(self) + " (and cards aboard it) in Used Pile");
            action.appendEffect(new PutCardFromLostPileInUsedPileEffect(action, playerId, self, true));
            for (PhysicalCard present : lost.getWasPresentWith()) {
                if (present != self
                        && GameUtils.getZoneFromZoneTop(present.getZone()) == Zone.LOST_PILE
                        && (present.getAttachedTo() == self || Filters.aboard(self).accepts(game, present))) {
                    action.appendEffect(new PutCardFromLostPileInUsedPileEffect(action, playerId, present, true));
                }
            }
            return Collections.singletonList(action);
        }
        return null;
    }

    private List<OptionalGameTextTriggerAction> getPlaceInUsedPileAction(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__OBSIDIAN_10_V__PLACE_IN_USED_PILE;
        if ((TriggerConditions.isAboutToLeaveTable(game, effectResult, self)
                || TriggerConditions.isAboutToBeLostIncludingAllCardsSituation(game, effectResult, self)
                || TriggerConditions.isAboutToBeForfeitedToLostPile(game, effectResult, self))
                && isAtEndorMobileSystemOrSector(game, self)) {
            final AboutToLeaveTableResult result = (AboutToLeaveTableResult) effectResult;
            final Collection<PhysicalCard> cardsAboard = Filters.filterAllOnTable(game, Filters.aboard(self));
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Place " + GameUtils.getFullName(self) + " in Used Pile");
            action.setActionMsg("Place " + GameUtils.getCardLink(self) + " (and cards aboard it) in Used Pile");
            action.appendEffect(
                    new PassthruEffect(action) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            result.getPreventableCardEffect().preventEffectOnCard(self);
                            for (PhysicalCard aboard : cardsAboard) {
                                result.getPreventableCardEffect().preventEffectOnCard(aboard);
                            }
                            if (!cardsAboard.isEmpty()) {
                                action.appendEffect(new PlaceCardsInUsedPileFromTableEffect(action, cardsAboard));
                            }
                            action.appendEffect(new PlaceCardInUsedPileFromTableEffect(action, self));
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }

    private boolean isAtEndorMobileSystemOrSector(SwccgGame game, PhysicalCard self) {
        if (GameConditions.isAtLocation(game, self, Filters.or(Filters.Endor_location, Filters.Endor_system, Filters.mobile_system, Filters.sector))) {
            return true;
        }
        PhysicalCard location = game.getModifiersQuerying().getLocationThatCardIsAt(game.getGameState(), self);
        if (location == null) {
            location = self.getAtLocation();
        }
        return location != null
                && Filters.or(Filters.Endor_location, Filters.Endor_system, Filters.mobile_system, Filters.sector).accepts(game, location);
    }
}
