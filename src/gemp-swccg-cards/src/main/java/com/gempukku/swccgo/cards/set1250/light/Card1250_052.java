package com.gempukku.swccgo.cards.set1250.light;

import com.gempukku.swccgo.cards.AbstractRebelRepublic;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.Agenda;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromLostPileEffect;
import com.gempukku.swccgo.logic.effects.choose.PlaceCardOutOfPlayFromLostPileEffect;
import com.gempukku.swccgo.logic.modifiers.AgendaModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.List;

/** Legacy Block 5 Senator Padme Amidala, printing 52. */
public class Card1250_052 extends AbstractRebelRepublic {
    public Card1250_052() {
        super(Side.LIGHT, 1, 3, 3, 4, 7, "Senator Padme Amidala", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_5, Rarity.V);
        setLore("Leader. Senator.");
        setGameText("Agendas: justice, rebellion. Once per turn, if at Senate with Bail, may lose 1 Force to deploy a unique (•), non-[Maintenance] Rebel of ability < 4 from Lost Pile. If just lost from same site as Vader, place her out of play; opponent loses 4 Force.");
        setPolitics(4);
        addPersona(Persona.AMIDALA);
        addIcons(Icon.EPISODE_I, Icon.WARRIOR);
        addKeywords(Keyword.LEADER, Keyword.SENATOR);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        return List.of(new AgendaModifier(self, Agenda.JUSTICE, Agenda.REBELLION));
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game,
            PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId id = GameTextActionId.LEGACY__SENATOR_PADME_AMIDALA__DEPLOY_REBEL;
        if (!GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, id)
                || !GameConditions.isAtLocation(game, self, Filters.Galactic_Senate)
                || !GameConditions.isWith(game, self, Filters.Bail)
                || !GameConditions.canDeployCardFromLostPile(game, playerId, self, id)) return null;

        TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, id);
        LoseForceEffect cost = new LoseForceEffect(action, playerId, 1, true);
        if (!cost.isPlayableInFull(game)) return null;
        action.setText("Lose 1 Force to deploy a Rebel from Lost Pile");
        action.appendUsage(new OncePerTurnEffect(action));
        action.appendCost(cost);
        action.appendEffect(new DeployCardFromLostPileEffect(action, Filters.and(Filters.Rebel,
                Filters.unique, Filters.not(Icon.MAINTENANCE), Filters.abilityLessThan(4)), false));
        return List.of(action);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextLeavesTableRequiredTriggers(SwccgGame game,
            EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.justLostFromLocation(game, effectResult, self, Filters.sameSiteAs(self, Filters.Vader))) {
            String owner = self.getOwner();
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Place out of play and make opponent lose 4 Force");
            action.appendEffect(new PlaceCardOutOfPlayFromLostPileEffect(action, owner, owner, self, false));
            action.appendEffect(new LoseForceEffect(action, game.getOpponent(owner), 4));
            return List.of(action);
        }
        return null;
    }
}
