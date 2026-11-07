package com.gempukku.swccgo.cards.set1271.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OccupiesCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DrawCardsIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.PlayCardResult;
import java.util.*;

public class Card1271_042 extends AbstractNormalEffect {
    public Card1271_042() {
        super(Side.LIGHT, 4, PlayCardZoneOption.ATTACHED, Title.S_foils, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_7_1, Rarity.V);
        setVirtualSuffix(true);
        setLore("'Lock S-foils in attack position.'");
        setGameText("Deploy on a Rebel Base system. Each time you deploy a Rebel starfighter simultaneously with a pilot character, may retrieve 1 Force and/or draw top card from Reserve Deck. Suspended if Death Star 'blown away.' (Immune to Alter and Crossfire while you occupy this system.)");
        addIcons(Icon.SPECIAL_EDITION);
        addKeywords(Keyword.DEPLOYS_ON_LOCATION);
    }
    @Override protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId option, boolean asReact) {
        return Filters.Rebel_Base_system;
    }
    @Override protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game, EffectResult result, PhysicalCard self, int sourceId) {
        if (!TriggerConditions.justDeployed(game, result, Filters.and(Filters.your(self), Filters.Rebel_starfighter))) return null;
        PhysicalCard pilot = ((PlayCardResult) result).getOtherPlayedCard();
        if (pilot == null || !Filters.and(Filters.your(self), Filters.character, Filters.pilot).accepts(game, pilot)) return null;
        OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, sourceId);
        action.setText("Retrieve 1 Force and/or draw top card");
        if (GameConditions.hasLostPile(game, playerId)) {
            action.appendEffect(new PlayoutDecisionEffect(action, playerId, new YesNoDecision("Retrieve 1 Force?") {
                @Override protected void yes() { action.insertEffect(new RetrieveForceEffect(action, playerId, 1)); }
            }));
        }
        if (GameConditions.hasReserveDeck(game, playerId)) {
            action.appendEffect(new PlayoutDecisionEffect(action, playerId, new YesNoDecision("Draw top card from Reserve Deck?") {
                @Override protected void yes() { action.insertEffect(new DrawCardsIntoHandFromReserveDeckEffect(action, playerId, 1)); }
            }));
        }
        return Collections.singletonList(action);
    }
    @Override protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        Condition occupySystem = new OccupiesCondition(self.getOwner(), Filters.and(Filters.system, Filters.hasAttached(self)));
        return Arrays.asList(new ImmuneToTitleModifier(self, occupySystem, Title.Alter), new ImmuneToTitleModifier(self, occupySystem, Title.Crossfire));
    }
    @Override public List<Modifier> getWhileInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = super.getWhileInPlayModifiers(game, self);
        Modifier suspension = new SuspendsCardModifier(self, self, new Condition() {
            @Override public boolean isFulfilled(GameState state, ModifiersQuerying query) {
                return GameConditions.isBlownAway(game, Filters.title(Title.Death_Star, true));
            }
        });
        // Suspension must remain registered after the card becomes inactive, as with Utinni Effect suspension.
        suspension.setPersistent(true);
        modifiers.add(suspension);
        return modifiers;
    }
}
