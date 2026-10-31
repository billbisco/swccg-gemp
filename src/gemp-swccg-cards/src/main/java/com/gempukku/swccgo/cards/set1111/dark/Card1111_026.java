package com.gempukku.swccgo.cards.set1111.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.conditions.ArmedWithCondition;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
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
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromLostPileEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DefenseValueModifier;
import com.gempukku.swccgo.logic.modifiers.IconModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.MayBeTargetedByModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.LostFromTableResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 11: Gathering Storm
 * Type: Character
 * Subtype: Imperial
 * Title: Lord Vader
 */
public class Card1111_026 extends AbstractImperial {
    public Card1111_026() {
        super(Side.DARK, 1, 8, 6, 8, 8, "Lord Vader", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_11, Rarity.V);
        setVirtualSuffix(true);
        setLore("Forgiving administrator of Imperial policy. Emperor Palpatine's most trusted leader. Believes converting Skywalker is key to the Alliance's downfall.");
        setGameText("Adds 3 to power of anything he pilots. May deploy a lightsaber just lost from same site on him (for free); may then use two weapons. Using Epic Duel, The Circle Is Now Complete may target any Jedi instead of Obi-Wan. Defense value +2 if armed. Immune to attrition < 5.");
        addPersona(Persona.VADER);
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.DEATH_STAR_II);
        addKeywords(Keyword.LEADER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        modifiers.add(new IconModifier(self, new ArmedWithCondition(self, Filters.lightsaber), Icon.WARRIOR));
        modifiers.add(new DefenseValueModifier(self, new ArmedWithCondition(self, Filters.lightsaber), 2));
        modifiers.add(new MayBeTargetedByModifier(self, Filters.Jedi, new OnTableCondition(self, Filters.Epic_Duel), Title.The_Circle_Is_Now_Complete));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 5));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OVS__LORD_VADER__DEPLOY_LIGHTSABER;

        if (TriggerConditions.justLostFromLocation(game, effectResult, Filters.lightsaber, Filters.sameSite(self))) {
            final PhysicalCard saber = ((LostFromTableResult) effectResult).getCard();
            if (saber != null) {
                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId, gameTextActionId);
                action.setText("Deploy " + GameUtils.getFullName(saber) + " on Vader");
                action.setActionMsg("Deploy " + GameUtils.getCardLink(saber) + " on " + GameUtils.getCardLink(self) + " for free");
                action.appendEffect(
                        new DeployCardToTargetFromLostPileEffect(action, saber, Filters.sameCardId(self), true, false));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
