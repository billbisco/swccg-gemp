package com.gempukku.swccgo.cards.set1106.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
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
import com.gempukku.swccgo.logic.effects.CaptureCharacterFromLostInSpaceOrWeatherVaneEffect;
import com.gempukku.swccgo.logic.effects.LoseCardsFromTableSimultaneouslyEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalForceGenerationModifier;
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
public class Card1106_049 extends AbstractNormalEffect {
    public Card1106_049() {
        super(Side.DARK, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Weather_Vane, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_6, Rarity.V);
        setVirtualSuffix(true);
        setLore("Not a good place to hang around.");
        setGameText("Deploy on table. Character here lost if new character arrives. Your Force generation is +2 while Bespin: Cloud City on table. During owner's control phase, they must relocate character here to a Cloud City site (if a bounty hunter on Cloud City, character captured instead). (Immune to Alter.)");
        addIcons(Icon.CLOUD_CITY);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new TotalForceGenerationModifier(self, new OnTableCondition(self, Filters.Bespin_Cloud_City), 2, self.getOwner()));
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
            boolean bountyHunterOnCloudCity = GameConditions.canSpot(game, self,
                    Filters.and(Filters.bounty_hunter, Filters.at(Filters.Cloud_City_location)));
            if (bountyHunterOnCloudCity) {
                PhysicalCard captureLocation = Filters.findFirstFromTopLocationsOnTable(game, Filters.Bespin_Cloud_City);
                if (captureLocation == null) {
                    captureLocation = Filters.findFirstFromTopLocationsOnTable(game, Filters.Cloud_City_location);
                }
                if (captureLocation != null) {
                    RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                    action.setText("Capture character");
                    action.setActionMsg("Capture " + GameUtils.getCardLink(attachedCharacter));
                    action.appendEffect(
                            new CaptureCharacterFromLostInSpaceOrWeatherVaneEffect(action, attachedCharacter, captureLocation));
                    return Collections.singletonList(action);
                }
            }

            Filter locationFilter = Filters.and(Filters.Cloud_City_site, Filters.locationCanBeRelocatedTo(attachedCharacter, false, false, true, 0, false));
            if (GameConditions.canSpotLocation(game, locationFilter)) {
                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Relocate character to a Cloud City site");
                action.setPerformingPlayer(self.getOwner());
                action.appendTargeting(
                        new ChooseCardOnTableEffect(action, self.getOwner(), "Choose Cloud City site", locationFilter) {
                            @Override
                            protected void cardSelected(PhysicalCard selectedSite) {
                                action.appendEffect(
                                        new RelocateFromWeatherVaneToLocation(action, attachedCharacter, selectedSite));
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
