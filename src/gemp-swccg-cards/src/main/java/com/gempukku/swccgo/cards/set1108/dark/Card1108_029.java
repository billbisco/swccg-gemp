package com.gempukku.swccgo.cards.set1108.dark;

import com.gempukku.swccgo.cards.AbstractImmediateEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayCardAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.PutCardFromHandOnLostPileEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Set 8
 * Type: Effect
 * Subtype: Immediate
 * Title: Cane Adiss (V)
 */
public class Card1108_029 extends AbstractImmediateEffect {
    public Card1108_029() {
        super(Side.DARK, 4, PlayCardZoneOption.ATTACHED, Title.Cane_Adiss, Uniqueness.RESTRICTED_2, ExpansionSet.VIRTUAL_ORIGINAL_SET_8, Rarity.V);
        setVirtualSuffix(true);
        setLore("Adventurous pilot. Boasts to Jabba that he has traveled to every uncharted planet in the galaxy. Has been hired by the Empire to keep an eye out for Rebel activity.");
        setGameText("If opponent just Force drained at a non-battleground location (or at any location for more than 3 Force), deploy on that location. To initiate a Force drain at same or related location, opponent must first place a card from hand in Lost Pile. (Immune to Control.)");
        addIcons(Icon.JABBAS_PALACE);
        addKeywords(Keyword.DEPLOYS_ON_LOCATION);
        addImmuneToCardTitle(Title.Control);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.location;
    }

    @Override
    protected List<PlayCardAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerId);
        if (TriggerConditions.forceDrainInitiatedBy(game, effectResult, opponent)
                && game.getGameState().getForceDrainState() != null) {
            PhysicalCard forceDrainLocation = game.getGameState().getForceDrainState().getLocation();
            boolean nonBattleground = Filters.non_battleground_location.accepts(game, forceDrainLocation);
            boolean moreThanThree = GameConditions.getForceDrainAmount(game) > 3;
            if (nonBattleground || moreThanThree) {
                PlayCardAction action = getPlayCardAction(playerId, game, self, self, false, 0, null, null, null, null, null, false, 0, Filters.sameLocationId(forceDrainLocation), null);
                if (action != null) {
                    return Collections.singletonList(action);
                }
            }
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(self.getOwner());
        // First-cut: extra cost is a required after-trigger on initiation (engine has no place-from-hand ExtraCostToForceDrain).
        if (TriggerConditions.forceDrainInitiatedBy(game, effectResult, opponent, Filters.sameOrRelatedLocation(self))
                && GameConditions.hasHand(game, opponent)) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Opponent places a card from hand in Lost Pile");
            action.appendEffect(
                    new PutCardFromHandOnLostPileEffect(action, opponent));
            return Collections.singletonList(action);
        }
        return null;
    }
}
