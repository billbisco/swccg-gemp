package com.gempukku.swccgo.cards.set1220.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployAsReactToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayMoveAsReactToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 2
 * Type: Character
 * Subtype: Rebel
 * Title: Princess Leia
 */
public class Card1220_097 extends AbstractRebel {
    public Card1220_097() {
        super(Side.LIGHT, 1, 3, 3, 4, 7, "Princess Leia", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("Prominent leader in the struggling Alliance. Former member of the Imperial Senate. Beginning to discover her true heritage. Likes scoundrels.");
        setGameText("May deploy or move as a 'react' to same site as an Imperial. Adds one battle destiny if with Han or Vader. During your deploy phase, may deploy one Leia Of Alderaan, Reflection, or a matching blaster on Leia from Reserve Deck; reshuffle. Immune to attrition < 4.");
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.CLOUD_CITY);
        addPersona(Persona.LEIA);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployAsReactToLocationModifier(self, Filters.sameSiteAs(self, Filters.Imperial)));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayMoveAsReactToLocationModifier(self, Filters.sameSiteAs(self, Filters.Imperial)));
        modifiers.add(new AddsBattleDestinyModifier(self, new WithCondition(self, Filters.or(Filters.Han, Filters.Vader)), 1));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 4));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__LEIA_V__DEPLOY_EFFECT_OR_BLASTER;

        if (GameConditions.isDuringYourPhase(game, self, Phase.DEPLOY)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            Filter effectFilter = Filters.or(Filters.Leia_Of_Alderaan, Filters.Reflection);
            final TopLevelGameTextAction effectAction = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            effectAction.setText("Deploy Leia Of Alderaan or Reflection");
            effectAction.setActionMsg("Deploy Leia Of Alderaan or Reflection from Reserve Deck");
            effectAction.appendEffect(
                    new DeployCardFromReserveDeckEffect(effectAction, effectFilter, true));
            actions.add(effectAction);

            Filter matchingBlaster = Filters.and(Filters.blaster, Filters.matchingWeaponForCharacter(self));
            final TopLevelGameTextAction blasterAction = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            blasterAction.setText("Deploy matching blaster on Leia");
            blasterAction.setActionMsg("Deploy a matching blaster on " + GameUtils.getCardLink(self) + " from Reserve Deck");
            blasterAction.appendEffect(
                    new DeployCardToTargetFromReserveDeckEffect(blasterAction, matchingBlaster, Filters.sameCardId(self), true));
            actions.add(blasterAction);
        }
        return actions;
    }
}
