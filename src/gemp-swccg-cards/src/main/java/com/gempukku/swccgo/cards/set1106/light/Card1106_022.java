package com.gempukku.swccgo.cards.set1106.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.RelocateFromWeatherVaneToLocation;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseCardsFromTableSimultaneouslyEffect;
import com.gempukku.swccgo.logic.effects.PutStackedCardInUsedPileEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.GenerateNoForceModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.RelocateToWeatherVaneResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 6: City In The Clouds
 * Type: Effect
 * Title: Weather Vane
 */
public class Card1106_022 extends AbstractNormalEffect {
    public Card1106_022() {
        super(Side.LIGHT, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Weather_Vane, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_6, Rarity.V);
        setVirtualSuffix(true);
        setLore("The metal rods extending from the bottom of Cloud City are part of the city's flotation system. Sensors detect the velocity of wind and the content of local clouds.");
        setGameText("Deploy on table. Character here lost if new character arrives. Opponent generates no Force at your cloud sectors. During owner's control phase, they must relocate character here to an unoccupied Cloud City site (place in Used Pile otherwise). (Immune to Alter.)");
        addIcons(Icon.CLOUD_CITY);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new GenerateNoForceModifier(self, Filters.and(Filters.your(self), Filters.cloud_sector), opponent));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.justRelocatedToWeatherVane(game, effectResult, Filters.character)) {
            PhysicalCard newCharacter = ((RelocateToWeatherVaneResult) effectResult).getCard();
            Collection<PhysicalCard> charactersToLose = Filters.filter(game.getGameState().getStackedCards(self), game, Filters.not(newCharacter));
            if (!charactersToLose.isEmpty()) {
                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Make character lost");
                action.setActionMsg("Make " + GameUtils.getAppendedNames(charactersToLose) + " lost");
                action.appendEffect(
                        new LoseCardsFromTableSimultaneouslyEffect(action, charactersToLose, false, true));
                return Collections.singletonList(action);
            }
        }

        if (TriggerConditions.isStartOfYourPhase(game, self, effectResult, Phase.CONTROL)
                && GameConditions.hasStackedCards(game, self, Filters.character)) {
            final PhysicalCard attachedCharacter = game.getGameState().getStackedCards(self).get(0);
            Filter locationFilter = Filters.and(Filters.Cloud_City_site, Filters.unoccupied,
                    Filters.locationCanBeRelocatedTo(attachedCharacter, false, false, true, 0, false));
            if (GameConditions.canSpotLocation(game, locationFilter)) {
                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Relocate character to an unoccupied Cloud City site");
                action.setPerformingPlayer(self.getOwner());
                action.appendTargeting(
                        new ChooseCardOnTableEffect(action, self.getOwner(), "Choose unoccupied Cloud City site", locationFilter) {
                            @Override
                            protected void cardSelected(PhysicalCard selectedSite) {
                                action.appendEffect(
                                        new RelocateFromWeatherVaneToLocation(action, attachedCharacter, selectedSite));
                            }
                        }
                );
                return Collections.singletonList(action);
            }

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Place character in Used Pile");
            action.setActionMsg("Place " + GameUtils.getCardLink(attachedCharacter) + " in Used Pile");
            action.appendEffect(
                    new PutStackedCardInUsedPileEffect(action, attachedCharacter.getOwner(), attachedCharacter, false));
            return Collections.singletonList(action);
        }
        return null;
    }
}
