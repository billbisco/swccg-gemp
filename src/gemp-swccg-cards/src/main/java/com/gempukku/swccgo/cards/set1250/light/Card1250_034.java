package com.gempukku.swccgo.cards.set1250.light;

import com.gempukku.swccgo.cards.AbstractRebelRepublic;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseCardsFromTableEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.AgendaModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/** Legacy Block 5 Light Bail Organa (35). */
public class Card1250_034 extends AbstractRebelRepublic {
    public Card1250_034() {
        super(Side.LIGHT, 3, 2, 3, 3, 5, "Bail Organa", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_5, Rarity.V);
        setPolitics(2);
        setLore("Leader. Senator.");
        setGameText("Agenda: rebellion. Adds one battle destiny with Leia. Your [Coruscant] senators and [Coruscant] Political Effects are lost. While at Senate: unique (•) Rebels of ability < 4 are power +2, and, if opponent just lost Force to a Force drain by a Rebel, may retrieve 1 Force.");
        addIcons(Icon.SPECIAL_EDITION, Icon.EPISODE_I);
        addPersona(Persona.BAIL);
        addKeywords(Keyword.LEADER, Keyword.SENATOR);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        return Collections.singletonList(new AgendaModifier(self, Agenda.REBELLION));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        return List.of(new AddsBattleDestinyModifier(self, new WithCondition(self, Filters.Leia), 1),
                new PowerModifier(self, Filters.and(Filters.unique, Filters.Rebel, Filters.abilityLessThan(4)),
                        new AtCondition(self, Filters.Galactic_Senate), 2));
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game,
            EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.isTableChanged(game, effectResult)) {
            Collection<PhysicalCard> toLose = Filters.filterActive(game, self, Filters.and(Filters.your(self), Icon.CORUSCANT,
                    Filters.or(Filters.senator, Filters.Political_Effect)));
            if (!toLose.isEmpty()) {
                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setSingletonTrigger(true);
                action.setText("Make your Coruscant senators and Political Effects lost");
                action.appendEffect(new LoseCardsFromTableEffect(action, toLose));
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game,
            EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        if (GameConditions.isAtLocation(game, self, Filters.Galactic_Senate)
                && TriggerConditions.justLostForceFromForceDrainAt(game, effectResult, game.getOpponent(playerId),
                        Filters.sameLocationAs(self, Filters.and(Filters.your(self), Filters.Rebel)), false)
                && GameConditions.hasLostPile(game, playerId)) {
            OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Retrieve 1 Force");
            action.appendEffect(new RetrieveForceEffect(action, playerId, 1));
            return Collections.singletonList(action);
        }
        return null;
    }
}
