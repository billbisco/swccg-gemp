package com.gempukku.swccgo.cards.set1230.dark;

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
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.IgnoresLocationDeploymentRestrictionsWhenDeployingToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 3
 * Type: Effect
 * Subtype: Immediate
 * Title: Cane Adiss
 */
public class Card1230_025 extends AbstractImmediateEffect {
    public Card1230_025() {
        super(Side.DARK, 4, PlayCardZoneOption.ATTACHED, Title.Cane_Adiss, Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_BLOCK_3, Rarity.V);
        setVirtualSuffix(true);
        setLore("Adventurous pilot. Boasts to Jabba that he has traveled to every uncharted planet in the galaxy. Has been hired by the Empire to keep an eye out for Rebel activity.");
        setGameText("If opponent just Force drained at their non-battleground site (or anywhere for more than 2 Force), deploy on that location. Your characters and vehicles may deploy (for -1 Force each) here regardless of location deployment restrictions. If you control this location, place this card in Used Pile; opponent loses 2 Force. (Immune to Control.)");
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
            boolean theirNonBattlegroundSite = Filters.and(Filters.site, Filters.non_battleground_location, Filters.occupies(opponent)).accepts(game, forceDrainLocation);
            boolean moreThanTwo = GameConditions.getForceDrainAmount(game) > 2;
            if (theirNonBattlegroundSite || moreThanTwo) {
                PlayCardAction action = getPlayCardAction(playerId, game, self, self, false, 0, null, null, null, null, null, false, 0, Filters.sameLocationId(forceDrainLocation), null);
                if (action != null) {
                    return Collections.singletonList(action);
                }
            }
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter yourCharactersAndVehicles = Filters.and(Filters.your(self), Filters.or(Filters.character, Filters.vehicle));
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, yourCharactersAndVehicles, -1, Filters.here(self)));
        modifiers.add(new IgnoresLocationDeploymentRestrictionsWhenDeployingToLocationModifier(self, yourCharactersAndVehicles, Filters.here(self)));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.controls(game, playerId, Filters.here(self))) {
            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Place in Used Pile; opponent loses 2 Force");
            action.appendEffect(
                    new PlaceCardInUsedPileFromTableEffect(action, self));
            action.appendEffect(
                    new LoseForceEffect(action, opponent, 2));
            return Collections.singletonList(action);
        }
        return null;
    }
}
