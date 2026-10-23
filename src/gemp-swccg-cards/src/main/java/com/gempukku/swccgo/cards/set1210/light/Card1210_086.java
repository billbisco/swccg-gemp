package com.gempukku.swccgo.cards.set1210.light;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeChokedModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeTargetedByModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotMoveModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 1
 * Type: Character
 * Subtype: Droid
 * Title: Artoo
 */
public class Card1210_086 extends AbstractDroid {
    public Card1210_086() {
        super(Side.LIGHT, 1, 4, 1, 5, "Artoo", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_1, Rarity.V);
        setVirtualSuffix(true);
        setAlternateDestiny(6);
        setLore("Counterpart to C-3PO. Spy. Obstinate, headstrong and always full of surprises. R2-D2 was an integral part of Luke Skywalker's rescue plans.");
        setGameText("Heroic. Death Star Plans ignores objective game text, but only draws one destiny. Leia may not move (or be carried) the turn she is released. Once per game, may deploy Trash Compactor from Reserve Deck; reshuffle. May not be Undercover, 'choked,' or targeted by Emperor.");
        addPersona(Persona.R2D2);
        addModelType(ModelType.ASTROMECH);
        addIcons(Icon.NAV_COMPUTER, Icon.JABBAS_PALACE);
        addKeywords(Keyword.SPY, Keyword.HEROIC);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ModifyGameTextModifier(self, Filters.Death_Star_Plans, ModifyGameTextType.LEGACY__ARTOO_V__DEATH_STAR_PLANS_IGNORES_OBJECTIVE_DRAWS_ONE));
        modifiers.add(new ImmuneToTitleModifier(self, Title.Undercover));
        modifiers.add(new MayNotBeChokedModifier(self));
        modifiers.add(new MayNotBeTargetedByModifier(self, Filters.Emperor));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__ARTOO_V__DEPLOY_TRASH_COMPACTOR;
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Trash Compactor from Reserve Deck");
            action.setActionMsg("Deploy Trash Compactor from Reserve Deck");
            action.appendUsage(
                    new OncePerGameEffect(action));
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.Trash_Compactor, true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.released(game, effectResult, Filters.Leia)) {
            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Prevent Leia from moving");
            action.setActionMsg("Leia may not move (or be carried) until end of turn");
            action.appendEffect(
                    new AddUntilEndOfTurnModifierEffect(action, new MayNotMoveModifier(self, Filters.Leia),
                            "Leia may not move until end of turn"));
            return Collections.singletonList(action);
        }
        return null;
    }
}
