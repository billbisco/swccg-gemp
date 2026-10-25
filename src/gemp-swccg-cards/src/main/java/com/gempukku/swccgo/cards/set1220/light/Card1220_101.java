package com.gempukku.swccgo.cards.set1220.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OnCondition;
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
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployToDagobahLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.NoForceLossFromCardModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 2
 * Type: Character
 * Subtype: Rebel
 * Title: Son Of Skywalker
 */
public class Card1220_101 extends AbstractRebel {
    public Card1220_101() {
        super(Side.LIGHT, 1, 5, 5, 5, 8, "Son Of Skywalker", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("Luke Skywalker. Son of Anakin. Seeker of Yoda. Levitator of rocks. Ignorer of advice. Incapable of impossible. Reckless is he.");
        setGameText("Adds 3 to power of anything he pilots. Luke's Lightsaber is immune to Overload. May deploy Luke's Lightsaber from Reserve Deck; reshuffle. While on Dagobah, you lose no Force to Your Destiny or Vengeance Of The Dark Prince. Immune to attrition < 4.");
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.DAGOBAH);
        addPersona(Persona.LUKE);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployToDagobahLocationModifier(self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        modifiers.add(new ImmuneToTitleModifier(self, Filters.Lukes_Lightsaber, Title.Overload));
        modifiers.add(new NoForceLossFromCardModifier(self, Filters.or(Filters.Your_Destiny, Filters.Vengeance_Of_The_Dark_Prince),
                new OnCondition(self, Title.Dagobah), playerId));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 4));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__SON_OF_SKYWALKER_V__DEPLOY_LUKES_LIGHTSABER;

        if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Luke's Lightsaber");
            action.setActionMsg("Deploy Luke's Lightsaber on " + GameUtils.getCardLink(self) + " from Reserve Deck");
            action.appendEffect(
                    new DeployCardToTargetFromReserveDeckEffect(action, Filters.Lukes_Lightsaber, Filters.sameCardId(self), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
