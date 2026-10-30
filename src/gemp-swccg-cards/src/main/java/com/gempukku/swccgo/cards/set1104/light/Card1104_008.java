package com.gempukku.swccgo.cards.set1104.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.IsPoweredModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 4: Battle Of Hoth
 * Type: Character
 * Subtype: Rebel
 * Title: General Carlist Rieekan
 */
public class Card1104_008 extends AbstractRebel {
    public Card1104_008() {
        super(Side.LIGHT, 1, 2, 2, 2, 5, "General Carlist Rieekan", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_4, Rarity.V);
        setVirtualSuffix(true);
        setLore("One of the original founders of the Rebel Alliance. Former civilian strategist with the House of Organa. Somber leader of Echo Base.");
        setGameText("Your artillery weapons at same and related sites are powered and forfeit +2. During your deploy phase, may ▼ one A New Secret Base, an artillery weapon, or a non-creature card with \"Hoth\" in game text.");
        addIcons(Icon.WARRIOR, Icon.HOTH);
        addKeywords(Keyword.GENERAL);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter artillery = Filters.and(Filters.your(self), Filters.artillery_weapon, Filters.at(Filters.sameOrRelatedSite(self)));
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new IsPoweredModifier(self, artillery));
        modifiers.add(new ForfeitModifier(self, artillery, 2));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OVS__RIEEKAN__DEPLOY_FROM_RESERVE;

        Filter downloadFilter = Filters.or(
                Filters.title("A New Secret Base"),
                Filters.artillery_weapon,
                Filters.and(Filters.not(Filters.creature), Filters.gameTextContains("Hoth")));

        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DEPLOY)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy card from Reserve Deck");
            action.setActionMsg("Deploy A New Secret Base, an artillery weapon, or a non-creature card with 'Hoth' in game text from Reserve Deck");
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, downloadFilter, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
