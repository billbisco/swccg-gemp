package com.gempukku.swccgo.cards.set1220.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
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
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.ModifyDestinyEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 2
 * Type: Character
 * Subtype: Alien
 * Title: Lando Calrissian
 */
public class Card1220_014 extends AbstractAlien {
    public Card1220_014() {
        super(Side.DARK, 1, 2, 3, 3, 4, "Lando Calrissian", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("Scoundrel and gambler. Petty administrator of a small Tibanna gas mining operation. Easily coerced. Has problems of his own. Had dealings with the Tonnika sisters - twice.");
        setGameText("Adds 3 to power of anything he pilots. Once per turn, while present at a battleground site, may use 1 Force to add or subtract 1 from a just drawn weapon or battle destiny (or from opponent's just drawn destiny).");
        addPersona(Persona.LANDO);
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.CLOUD_CITY);
        addKeywords(Keyword.GAMBLER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerId);
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__LANDO_V__MODIFY_DESTINY;
        boolean weaponOrBattle = TriggerConditions.isWeaponDestinyJustDrawn(game, effectResult)
                || TriggerConditions.isBattleDestinyJustDrawn(game, effectResult);
        boolean opponentDestiny = TriggerConditions.isDestinyJustDrawnBy(game, effectResult, opponent);
        if ((weaponOrBattle || opponentDestiny)
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.isPresentAt(game, self, Filters.battleground_site)
                && GameConditions.canUseForce(game, playerId, 1)) {

            List<OptionalGameTextTriggerAction> actions = new LinkedList<OptionalGameTextTriggerAction>();

            final OptionalGameTextTriggerAction addAction = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            addAction.setText("Add 1 to destiny");
            addAction.appendUsage(
                    new OncePerTurnEffect(addAction));
            addAction.appendCost(
                    new UseForceEffect(addAction, playerId, 1));
            addAction.appendEffect(
                    new ModifyDestinyEffect(addAction, 1));
            actions.add(addAction);

            final OptionalGameTextTriggerAction subtractAction = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            subtractAction.setText("Subtract 1 from destiny");
            subtractAction.appendUsage(
                    new OncePerTurnEffect(subtractAction));
            subtractAction.appendCost(
                    new UseForceEffect(subtractAction, playerId, 1));
            subtractAction.appendEffect(
                    new ModifyDestinyEffect(subtractAction, -1));
            actions.add(subtractAction);

            return actions;
        }
        return null;
    }
}
