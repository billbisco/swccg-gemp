package com.gempukku.swccgo.cards.set1240.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
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
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayingCardEffect;
import com.gempukku.swccgo.cards.effects.StackCardFromVoidEffect;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 4: Expanded Universe
 * Type: Location
 * Subtype: Site
 * Title: Carida: ISB Training Academy
 */
public class Card1240_049 extends AbstractSite {
    public Card1240_049() {
        super(Side.DARK, "Carida: ISB Training Academy", Title.Carida, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_4, Rarity.V);
        setLocationLightSideGameText("Imperial Council Members are forfeit +2 here. Immune to Revolution.");
        setLocationDarkSideGameText("Once per game, if opponent just played an Interrupt with 'Rebel' in title, may stack it here.");
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcon(Icon.DARK_FORCE, 1);
        addIcons(Icon.LEGACY_BLOCK_4, Icon.SPECIAL_EDITION, Icon.EXTERIOR_SITE, Icon.PLANET, Icon.GRABBER);
        addImmuneToCardTitle(Title.Revolution);
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForfeitModifier(self, Filters.and(Keyword.IMPERIAL_COUNCIL_MEMBER, Filters.here(self)), 2));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextDarkSideOptionalBeforeTriggers(String playerOnDarkSideOfLocation, SwccgGame game, Effect effect, PhysicalCard self, int gameTextSourceCardId) {
        if (!(effect instanceof RespondablePlayingCardEffect) || !(effect.getAction() instanceof PlayInterruptAction)) {
            return null;
        }
        PhysicalCard played = ((RespondablePlayingCardEffect) effect).getCard();
        if (!TriggerConditions.isPlayingCard(game, effect, game.getOpponent(playerOnDarkSideOfLocation), Filters.and(Filters.Interrupt, Filters.titleContains("Rebel")))
                || !GameConditions.canBeGrabbed(game, self, played)
                || !GameConditions.isOncePerGame(game, self, GameTextActionId.OTHER_CARD_ACTION_1)) {
            return null;
        }
        final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerOnDarkSideOfLocation, gameTextSourceCardId, GameTextActionId.OTHER_CARD_ACTION_1);
        action.setText("Stack " + GameUtils.getFullName(played));
        action.appendUsage(new OncePerGameEffect(action));
        action.appendEffect(new StackCardFromVoidEffect(action, played, self));
        return Collections.singletonList(action);
    }
}
