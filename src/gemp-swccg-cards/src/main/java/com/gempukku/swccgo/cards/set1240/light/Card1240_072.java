package com.gempukku.swccgo.cards.set1240.light;

import com.gempukku.swccgo.cards.AbstractRebelRepublic;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.Agenda;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.choose.PlayStackedDefensiveShieldEffect;
import com.gempukku.swccgo.logic.modifiers.AgendaModifier;
import com.gempukku.swccgo.logic.modifiers.AttritionModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Block 4: Expanded Universe
 * Type: Character
 * Subtype: Rebel
 * Title: Senator Leia Organa
 */
public class Card1240_072 extends AbstractRebelRepublic {
    public Card1240_072() {
        super(Side.LIGHT, 2, 3, 3, 3, 5, "Senator Leia Organa", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_4, Rarity.V);
        setLore("Adopted by Bail Organa. Former senator of Alderaan. Natural leader. Helped steal the Death Star plans. A key figure in the Rebel Alliance.");
        setGameText("Agendas: justice, rebellion. When deployed, may play a Defensive Shield from under your Starting Effect (as if from hand). While aboard Alderaan Consular Ship, it is immune to Lateral Damage and attrition. Attrition against opponent is +2 here.");
        setPolitics(1);
        addIcons(Icon.SPECIAL_EDITION, Icon.WARRIOR, Icon.LEGACY_BLOCK_4);
        addPersona(Persona.LEIA);
        addKeywords(Keyword.SENATOR, Keyword.LEADER, Keyword.FEMALE);
        setSpecies(Species.ALDERAANIAN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        Filter carrier = Filters.and(Filters.title("Alderaan Consular Ship"), Filters.hasAboard(self));
        return Arrays.asList(
                new AgendaModifier(self, Agenda.JUSTICE, Agenda.REBELLION),
                new ImmuneToTitleModifier(self, carrier, Title.Lateral_Damage),
                new ImmuneToAttritionModifier(self, carrier),
                new AttritionModifier(self, Filters.here(self), 2, game.getOpponent(self.getOwner())));
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.justDeployed(game, effectResult, self)) {
            PhysicalCard startingEffect = Filters.findFirstActive(game, self, Filters.and(Filters.your(self), Filters.Starting_Effect));
            if (startingEffect != null && GameConditions.hasStackedCards(game, startingEffect, Filters.and(Filters.Defensive_Shield, Filters.playable(self)))) {
                OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Play a Defensive Shield");
                action.appendEffect(new PlayStackedDefensiveShieldEffect(action, startingEffect));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
