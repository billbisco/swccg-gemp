package com.gempukku.swccgo.cards.set1210.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.CaptureCharacterFromLostPileEffect;
import com.gempukku.swccgo.logic.effects.CaptureStarshipEffect;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.results.LostFromTableResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Block 1
 * Type: Effect
 * Title: Besieged
 */
public class Card1210_032 extends AbstractNormalEffect {
    public Card1210_032() {
        super(Side.DARK, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Besieged, Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_BLOCK_1, Rarity.V);
        setVirtualSuffix(true);
        setLore("Stormtroopers blasted through the main airlock of the Tantive IV. The Rebel soldiers' attempt to defend the intrusion was no match for the Empire's superior firepower.");
        setGameText("Deploy on table. If opponent just forfeited a character or unpiloted starship at same system or sector as your Imperial capital starship, you may draw destiny. Character or starship is captured if destiny > its destiny number. (Immune to Alter.)");
        addIcons(Icon.A_NEW_HOPE);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerId);
        Filter forfeitedFilter = Filters.and(Filters.opponents(self), Filters.or(Filters.character, Filters.and(Filters.starship, Filters.unpiloted)));
        Filter locationFilter = Filters.and(Filters.or(Filters.system, Filters.sector),
                Filters.sameLocationAs(self, Filters.and(Filters.your(self), Filters.Imperial_starship, Filters.capital_starship)));

        if (TriggerConditions.justForfeited(game, effectResult, opponent, forfeitedFilter)
                && TriggerConditions.justForfeitedToLostPileFromLocation(game, effectResult, forfeitedFilter, locationFilter)) {
            final LostFromTableResult lostResult = (LostFromTableResult) effectResult;
            final PhysicalCard forfeited = lostResult.getCard();
            final PhysicalCard location = lostResult.getFromLocation();

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId, GameTextActionId.LEGACY__BESIEGED_V__CAPTURE);
            action.setText("Draw destiny to capture " + GameUtils.getFullName(forfeited));
            action.appendEffect(
                    new DrawDestinyEffect(action, playerId) {
                        @Override
                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                            GameState gameState = game.getGameState();
                            gameState.sendMessage("Destiny: " + (totalDestiny != null ? GuiUtils.formatAsString(totalDestiny) : "Failed destiny draw"));
                            float destinyNumber = game.getModifiersQuerying().getDestiny(gameState, forfeited);
                            gameState.sendMessage("Destiny number: " + GuiUtils.formatAsString(destinyNumber));
                            if (totalDestiny != null && totalDestiny > destinyNumber) {
                                gameState.sendMessage("Result: Captured");
                                if (Filters.character.accepts(game, forfeited)) {
                                    action.appendEffect(
                                            new CaptureCharacterFromLostPileEffect(action, forfeited, location));
                                }
                                else {
                                    action.appendEffect(
                                            new CaptureStarshipEffect(action, forfeited, self));
                                }
                            }
                            else {
                                gameState.sendMessage("Result: Failed");
                            }
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}
