package com.gempukku.swccgo.cards.set1281.light;

import com.gempukku.swccgo.cards.AbstractCapitalStarship;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.HasPilotingCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 8.1
 * Type: Starship
 * Subtype: Capital
 * Title: Errant Venture
 */
public class Card1281_045 extends AbstractCapitalStarship {
    public Card1281_045() {
        super(Side.LIGHT, 2, 6, 6, 6, null, 3, 8, "Errant Venture", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_8_1, Rarity.V);
        setGameText("May add 6 pilots. Permanent pilot provides ability of 3. Unless your [Reflections 2] objective on table, once per game, may deploy a non-[Maintenance] scoundrel aboard from Reserve Deck; reshuffle and while Corran or a Terrik piloting, immune to attrition < 7.");
        addIcons(Icon.INDEPENDENT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK, Icon.PILOT);
        addModelType(ModelType.IMPERIAL_CLASS_STAR_DESTROYER);
        setPilotCapacity(6);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(3) {});
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self,
                new HasPilotingCondition(self, Filters.or(Filters.persona(Persona.CORRAN_HORN),
                        Filters.titleContains("Terrik"), Filters.persona(Persona.BOOSTER))), 7));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__ERRANT_VENTURE__DEPLOY_SCOUNDREL_ABOARD;
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && !GameConditions.canSpot(game, self, Filters.and(Filters.your(playerId), Filters.Objective, Icon.REFLECTIONS_II))
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy a scoundrel aboard from Reserve Deck");
            action.setActionMsg("Deploy a non-[Maintenance] scoundrel aboard from Reserve Deck");
            action.appendUsage(new OncePerGameEffect(action));
            action.appendEffect(new DeployCardToTargetFromReserveDeckEffect(action,
                    Filters.and(Filters.scoundrel, Filters.not(Icon.MAINTENANCE)), Filters.sameCardId(self), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
