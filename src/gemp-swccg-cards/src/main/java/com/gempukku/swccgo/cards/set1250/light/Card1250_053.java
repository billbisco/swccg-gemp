package com.gempukku.swccgo.cards.set1250.light;

import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OnCondition;
import com.gempukku.swccgo.cards.conditions.PlayersTurnCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseCardsFromTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DrawCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AgendaModifier;
import com.gempukku.swccgo.logic.modifiers.MayMakeAdditionalMovesModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.UsedInterruptModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/** Legacy Block 5 Light Supreme Chancellor Palpatine (53). */
public class Card1250_053 extends AbstractRepublic {
    public Card1250_053() {
        super(Side.LIGHT, 0, 4, 2, 4, 6, "Supreme Chancellor Palpatine", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_5, Rarity.V);
        setPolitics(5);
        setLore("Leader.");
        setGameText("Agendas: ambition, order. While on Coruscant, Ambush is a Used Interrupt and, each time you initiate a Force drain with a clone, may draw top card from Reserve Deck. During your turn, may make an additional regular move. Valorum is lost.");
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I);
        addPersona(Persona.SIDIOUS);
        addKeyword(Keyword.LEADER);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        return Collections.singletonList(new AgendaModifier(self, Agenda.AMBITION, Agenda.ORDER));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        return List.of(new UsedInterruptModifier(self, Filters.Ambush, new OnCondition(self, Title.Coruscant)),
                new MayMakeAdditionalMovesModifier(self, self, new PlayersTurnCondition(self.getOwner()), 1));
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game,
            EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.forceDrainInitiatedBy(game, effectResult, playerId)
                && GameConditions.isOnSystem(game, self, Title.Coruscant)
                && GameConditions.hasReserveDeck(game, playerId)
                && GameConditions.canSpot(game, self, Filters.and(Filters.your(self), Filters.clone,
                        Filters.at(game.getGameState().getForceDrainLocation())))) {
            OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Draw top card of Reserve Deck");
            action.appendEffect(new DrawCardIntoHandFromReserveDeckEffect(action, playerId));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game,
            EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.isTableChanged(game, effectResult)) {
            Collection<PhysicalCard> valorum = Filters.filterActive(game, self, Filters.Valorum);
            if (!valorum.isEmpty()) {
                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setSingletonTrigger(true);
                action.setText("Make Valorum lost");
                action.appendEffect(new LoseCardsFromTableEffect(action, valorum, true));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
