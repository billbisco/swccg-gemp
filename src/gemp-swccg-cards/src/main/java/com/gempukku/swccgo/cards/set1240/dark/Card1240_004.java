package com.gempukku.swccgo.cards.set1240.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.common.Agenda;
import com.gempukku.swccgo.common.DestinyType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
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
import com.gempukku.swccgo.logic.effects.ModifyDestinyEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.AgendaModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.DestinyDrawnResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 4
 * Type: Character
 * Subtype: Imperial
 * Title: Colonel Wullf Yularen
 */
public class Card1240_004 extends AbstractImperial {
    public Card1240_004() {
        super(Side.DARK, 2, 2, 1, 2, 5, Title.Yularen, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_4, Rarity.V);
        setVirtualSuffix(true);
        setInfluence(2);
        setLore("Imperial Security Bureau (ISB) officer assigned to brief Tarkin. Also ordered to ensure absolute loyalty to the Emperor. Leader. Will stop at nothing to fulfill the Emperor's will.");
        setGameText("Imperial Council Member. Agendas: logistics. ISB Agents are forfeit +1. While at Conference Room with Sim Aloo, may use 1 Force to subtract 1 from an opponent's just drawn non-Epic Event destiny.");
        addIcons(Icon.WARRIOR);
        addKeywords(Keyword.IMPERIAL_COUNCIL_MEMBER, Keyword.LEADER);
        addPersona(Persona.YULAREN);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AgendaModifier(self, Agenda.LOGISTICS));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForfeitModifier(self, Filters.ISB_agent, 1));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerId);
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        if (TriggerConditions.isDestinyJustDrawnBy(game, effectResult, opponent)
                && effectResult instanceof DestinyDrawnResult
                && isNonEpicEventDestiny((DestinyDrawnResult) effectResult)
                && GameConditions.isAtLocation(game, self, Filters.Death_Star_Conference_Room)
                && GameConditions.isWith(game, self, Filters.title("Sim Aloo"))
                && GameConditions.canUseForce(game, playerId, 1)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Subtract 1 from opponent's destiny");
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            action.appendEffect(
                    new ModifyDestinyEffect(action, -1));
            return Collections.singletonList(action);
        }
        return null;
    }

    private boolean isNonEpicEventDestiny(DestinyDrawnResult result) {
        DestinyType type = result.getDestinyType();
        return type != DestinyType.EPIC_EVENT_DESTINY
                && type != DestinyType.EPIC_EVENT_AND_WEAPON_DESTINY;
    }
}
