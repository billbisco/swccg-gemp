package com.gempukku.swccgo.cards.set1105.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.EachTrainingDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployToDagobahLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 5: A Dark Time
 * Type: Character
 * Subtype: Rebel
 * Title: Son Of Skywalker
 */
public class Card1105_021 extends AbstractRebel {
    public Card1105_021() {
        super(Side.LIGHT, 1, 5, 5, 5, 8, "Son Of Skywalker", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_5, Rarity.V);
        setVirtualSuffix(true);
        setLore("Luke Skywalker. Son of Anakin. Seeker of Yoda. Levitator of rocks. Ignorer of advice. Incapable of impossible. Reckless is he.");
        setGameText("[Pilot] 2. Deploys -3 to Dagobah. You must first lose 1 Force to substitute a destiny draw using Jedi Test #5. His training destiny draws are +1. During your deploy phase, may deploy a card with \"training destiny\" in its game text from Reserve Deck; reshuffle. Immune to attrition < 4.");
        addIcons(Icon.DAGOBAH, Icon.PILOT, Icon.WARRIOR);
        addPersona(Persona.LUKE);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployToDagobahLocationModifier(self));
        modifiers.add(new DeployCostToLocationModifier(self, -3, Filters.Dagobah_location));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new EachTrainingDestinyModifier(self, Filters.jediTestTargetingApprentice(Filters.sameCardId(self)), 1));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 4));
        modifiers.add(new ModifyGameTextModifier(self, Filters.Jedi_Test_5, ModifyGameTextType.SON_OF_SKYWALKER_V__LOSE_1_FORCE_TO_SUBSTITUTE_DESTINY));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OVS__SON_OF_SKYWALKER__DEPLOY_TRAINING_DESTINY;

        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DEPLOY)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy card with training destiny from Reserve Deck");
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.gameTextContains("training destiny"), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
