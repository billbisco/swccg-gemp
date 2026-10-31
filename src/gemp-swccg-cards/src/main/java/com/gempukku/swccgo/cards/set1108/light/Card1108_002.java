package com.gempukku.swccgo.cards.set1108.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.PresentWithCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.DestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 8
 * Type: Character
 * Subtype: Alien
 * Title: Aved Luun
 */
public class Card1108_002 extends AbstractAlien {
    public Card1108_002() {
        super(Side.LIGHT, 3, 2, 1, 3, 4, Title.Aved_Luun, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_8, Rarity.V);
        setVirtualSuffix(true);
        setLore("Shaman of her Jawa tribe. Mate of Kalit. Suspects betrayal from her mate's rival, Wittin.");
        setGameText("Deploys only on Tatooine. Adds one battle destiny with Kalit. Your Jawas and sandcrawlers are destiny +2. May deploy Jawa Camp, Sergeant Doallyn, or Thedit on table from Reserve Deck; reshuffle.");
        addIcons(Icon.JABBAS_PALACE);
        addKeywords(Keyword.FEMALE);
        setSpecies(Species.JAWA);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Deploys_on_Tatooine;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsBattleDestinyModifier(self, new PresentWithCondition(self, Filters.Kalit), 1));
        modifiers.add(new DestinyModifier(self, Filters.and(Filters.your(self), Filters.or(Filters.Jawa, Filters.sandcrawler)), 2));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__AVED_LUUN_V__DEPLOY_CARD;

        if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy card from Reserve Deck");
            action.setActionMsg("Deploy Jawa Camp, Sergeant Doallyn, or Thedit from Reserve Deck");
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.or(Filters.Jawa_Camp, Filters.Doallyn, Filters.title(Title.Thedit)), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
