package com.gempukku.swccgo.cards.set1230.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardAboardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DrawCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployWithInsteadOfMatchingStarfighterUsingCombatResponseModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 3
 * Type: Character
 * Subtype: Alien
 * Title: Wooof
 */
public class Card1230_021 extends AbstractAlien {
    public Card1230_021() {
        super(Side.DARK, 3, 2, 2, 2, 4, Title.Wooof, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_3, Rarity.V);
        setVirtualSuffix(true);
        setLore("Kadas'sa'Nikto. One of Jabba's best pilots. Often pilots Jabba's space yacht. Smuggler. Prefers to be flying combat starfighters.");
        setGameText("Adds 3 to power of anything he pilots. Wooof may deploy with any cruiser using Combat Response. When deployed, may draw top card from Reserve Deck. During your deploy phase, if piloting Jabba's Space Cruiser, may deploy one Jabba aboard (for free) from Reserve Deck; reshuffle.");
        addIcons(Icon.PILOT, Icon.JABBAS_PALACE);
        addKeywords(Keyword.SMUGGLER);
        setSpecies(Species.NIKTO);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployWithInsteadOfMatchingStarfighterUsingCombatResponseModifier(self, Filters.cruiser));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.justDeployed(game, effectResult, self)
                && GameConditions.hasReserveDeck(game, playerId)) {
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Draw top card of Reserve Deck");
            action.appendEffect(
                    new DrawCardIntoHandFromReserveDeckEffect(action, playerId));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__WOOOF_V__DEPLOY_JABBA;
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DEPLOY)
                && Filters.piloting(Filters.Jabbas_Space_Cruiser).accepts(game, self)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Jabba aboard from Reserve Deck");
            action.setActionMsg("Deploy Jabba aboard Jabba's Space Cruiser from Reserve Deck");
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            action.appendEffect(
                    new DeployCardAboardFromReserveDeckEffect(action, Filters.Jabba, Filters.Jabbas_Space_Cruiser, true, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
