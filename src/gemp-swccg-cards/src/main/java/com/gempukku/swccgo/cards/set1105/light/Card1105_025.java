package com.gempukku.swccgo.cards.set1105.light;

import com.gempukku.swccgo.cards.AbstractJediMaster;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ForceRetrievalFromCardMayNotBeCanceledModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployToDagobahLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotContributeToForceRetrievalModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 5: A Dark Time
 * Type: Character
 * Subtype: Jedi Master
 * Title: Yoda
 */
public class Card1105_025 extends AbstractJediMaster {
    public Card1105_025() {
        super(Side.LIGHT, 1, 4, 3, 7, 9, "Yoda", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_5, Rarity.V);
        setVirtualSuffix(true);
        setLore("Help you I can, yes. For 800 years have I trained Jedi. Judge me by my size do you? Mm? And well you should not! For my ally is the Force... and a powerful ally it is.");
        setGameText("May deploy to Dagobah. Retrieve 3 Force (ignore objective game text that prevents retrieval) when a Jedi Test is completed (may not be canceled except by Secret Plans). Retrieve no Force from Save You It Can. May deploy one Yoda's Gimer Stick from Reserve Deck; reshuffle.");
        addIcons(Icon.DAGOBAH);
        addPersona(Persona.YODA);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployToDagobahLocationModifier(self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceRetrievalFromCardMayNotBeCanceledModifier(self, self, self.getOwner()));
        modifiers.add(new MayNotContributeToForceRetrievalModifier(self, Filters.Save_You_It_Can));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.jediTestCompleted(game, effectResult, Filters.any)) {
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Retrieve 3 Force");
            action.appendEffect(
                    new RetrieveForceEffect(action, playerId, 3));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OVS__YODA__DEPLOY_GIMER_STICK;

        if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Yoda's Gimer Stick from Reserve Deck");
            action.appendEffect(
                    new DeployCardToTargetFromReserveDeckEffect(action, Filters.title("Yoda's Gimer Stick"), Filters.Yoda, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
