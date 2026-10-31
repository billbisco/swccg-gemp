package com.gempukku.swccgo.cards.set1107.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromBottomOfForcePileEffect;
import com.gempukku.swccgo.logic.modifiers.DeploysFreeToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.KeywordModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotHaveGameTextCanceledModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 7: Shadows Of The Empire
 * Type: Character
 * Subtype: Rebel
 * Title: Chewbacca, Protector (V)
 */
public class Card1107_001 extends AbstractRebel {
    public Card1107_001() {
        super(Side.LIGHT, 1, 3, 6, 2, 7, "Chewbacca, Protector", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_7, Rarity.V);
        setVirtualSuffix(true);
        setArmor(4);
        setLore("Chewie honored his life debt to Han by protecting Leia. Posed as the mercenary Snoova while on Coruscant. Smuggler. Wookiee.");
        setGameText("Deploys free to Coruscant and is a spy while there. During your deploy phase, may ▼ one Boushh or Vibro-Ax (on Chewie). While with Leia, her game text may not be canceled and, once per turn, may take the bottom card of Force Pile into hand.");
        addPersona(Persona.CHEWIE);
        addIcons(Icon.REFLECTIONS_II, Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.SMUGGLER);
        setSpecies(Species.WOOKIEE);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeploysFreeToLocationModifier(self, Filters.Coruscant_location));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new KeywordModifier(self, new AtCondition(self, Filters.Coruscant_location), Keyword.SPY));
        modifiers.add(new MayNotHaveGameTextCanceledModifier(self, Filters.Leia, new WithCondition(self, Filters.Leia)));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();
        GameTextActionId gameTextActionId = GameTextActionId.OVS__CHEWBACCA_PROTECTOR__DEPLOY_BOUSHH_OR_VIBRO_AX;

        if (GameConditions.isDuringYourPhase(game, self, Phase.DEPLOY)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            Filter boushh = Filters.title(Title.Boushh);
            final TopLevelGameTextAction boushhAction = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            boushhAction.setText("Deploy Boushh from Reserve Deck");
            boushhAction.setActionMsg("Deploy Boushh from Reserve Deck");
            boushhAction.appendEffect(
                    new DeployCardFromReserveDeckEffect(boushhAction, boushh, true));
            actions.add(boushhAction);

            final TopLevelGameTextAction axAction = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            axAction.setText("Deploy Vibro-Ax on Chewie");
            axAction.setActionMsg("Deploy a Vibro-Ax on " + GameUtils.getCardLink(self) + " from Reserve Deck");
            axAction.appendEffect(
                    new DeployCardToTargetFromReserveDeckEffect(axAction, Filters.Vibro_Ax, Filters.sameCardId(self), true));
            actions.add(axAction);
        }

        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId)
                && GameConditions.canSpot(game, self, Filters.and(Filters.Leia, Filters.with(self)))
                && GameConditions.hasForcePile(game, playerId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Take bottom card of Force Pile into hand");
            action.appendUsage(
                    new OncePerTurnEffect(action));
            action.appendEffect(
                    new TakeCardIntoHandFromBottomOfForcePileEffect(action, playerId));
            actions.add(action);
        }
        return actions;
    }
}
