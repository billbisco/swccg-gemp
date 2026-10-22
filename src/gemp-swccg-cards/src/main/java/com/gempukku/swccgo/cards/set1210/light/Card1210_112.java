package com.gempukku.swccgo.cards.set1210.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.ChooseEffectEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.MayNotMoveOrBattleUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.StandardEffect;
import com.gempukku.swccgo.logic.timing.results.PlayCardResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 1
 * Type: Character
 * Subtype: Rebel
 * Title: Red Leader
 */
public class Card1210_112 extends AbstractRebel {
    public Card1210_112() {
        super(Side.LIGHT, 1, 2, 2, 2, 5, "Red Leader", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_1, Rarity.V);
        setVirtualSuffix(true);
        setLore("X-wing pilot Garven Dreis. Led Red Squadron at the Battle of Yavin, and fired an unsuccessful shot at the Death Star thermal exhaust port. Served at Dantooine Rebel base.");
        setGameText("Adds 2 to power of anything he pilots. While piloting at Dantooine system, when opponent deploys a character to a related site, they choose: opponent must lose 1 Force. OR that character may not battle or move this turn.");
        addPersona(Persona.RED_LEADER);
        addIcons(Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.RED_SQUADRON, Keyword.LEADER);
        setMatchingStarshipFilter(Filters.Red_1);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        final String opponent = game.getOpponent(self.getOwner());

        if (GameConditions.isPiloting(game, self, Filters.any)
                && GameConditions.isAtLocation(game, self, Filters.Dantooine_system)
                && TriggerConditions.justDeployedToLocation(game, effectResult, opponent, Filters.character, Filters.relatedSite(self))) {
            PlayCardResult playCardResult = (PlayCardResult) effectResult;
            final PhysicalCard deployed = playCardResult.getPlayedCard();

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Make opponent choose penalty");
            action.setActionMsg("Make opponent lose 1 Force or prevent " + GameUtils.getCardLink(deployed) + " from battling or moving until end of turn");
            List<StandardEffect> effectsToChoose = new ArrayList<StandardEffect>();
            effectsToChoose.add(new LoseForceEffect(action, opponent, 1));
            effectsToChoose.add(new MayNotMoveOrBattleUntilEndOfTurnEffect(action, deployed) {
                @Override
                public String getText(SwccgGame game) {
                    return GameUtils.getCardLink(deployed) + " may not battle or move until end of turn";
                }
            });
            action.appendEffect(
                    new ChooseEffectEffect(action, opponent, effectsToChoose));
            return Collections.singletonList(action);
        }
        return null;
    }
}
