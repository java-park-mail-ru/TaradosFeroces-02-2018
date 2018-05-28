package application.services;


import application.models.User;
import application.party.Party;
import application.party.messages.out.PartyView;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;


@Service
public class PartyService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PartyService.class);
    private static final String LOG_TAB_1 = "    ";

    private final ConcurrentHashMap<Long, Party> parties = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, Long> userIdToPartyId = new ConcurrentHashMap<>();

    @NotNull
    private NotificationService notificationService;

    public PartyService(@NotNull NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    public boolean addToPartyWithLeader(@NotNull User user, @NotNull User leader) {
        final Long partyId = userIdToPartyId.getOrDefault(user.getId(), null);

        if (partyId != null) {
            final Party oldParty = this.getPartyWithLeader(partyId);
            if (oldParty == null) {
                LOGGER.warn(LOG_TAB_1 + ": leader.id -> null party");
            } else {
                LOGGER.info(LOG_TAB_1 + ": leave old part (party.leader=" + oldParty.getLeader().getId() + ")");
                oldParty.removeUser(user);
                notificationService.sendLeavePartyNotification(user);
            }
        }

        Party party = this.getPartyWithLeader(leader.getId());
        if (party == null) {
            party = this.createPartyWithLeader(leader);

            if (party == null) {
                LOGGER.warn(LOG_TAB_1 + ": leader.id -> null party (leader.id=" + leader.getId() + ")");
                return false;
            } else {
                LOGGER.info(LOG_TAB_1 + ": created party with leader (leader.id=" + leader.getId() + ")");
            }
        }

        party.addUser(user);
        userIdToPartyId.put(user.getId(), leader.getId());

        final PartyView partyView = new PartyView(party);

        party.getAllIds().forEach(id -> notificationService.sendMessage(id, partyView));

        return true;
    }


    public Party createPartyWithLeader(@NotNull User user) {
        LOGGER.info("createParty: leader.id=" + user.getId() + ", .login=" + user.getLogin());
        LOGGER.info("           : before operations: parties.size=" + parties.size());
        LOGGER.info("                              : userIdToPartyId.size=" + userIdToPartyId.size());

        parties.put(user.getId(), new Party(user));
        userIdToPartyId.put(user.getId(), user.getId());

        LOGGER.info("           : after operations: parties.size=" + parties.size());
        LOGGER.info("                             : userIdToPartyId.size=" + userIdToPartyId.size());
        LOGGER.info("createParty: done");

        return parties.get(user.getId());
    }

    public Long getPartyLeaderId(@NotNull User user) {
        LOGGER.info("getPartyLeaderId: user.id=" + user.getId() + ", .login=" + user.getLogin());

        final Long partyLeaderId = userIdToPartyId.getOrDefault(user.getId(), null);
        LOGGER.info("                : partyLeaderId=" + ((partyLeaderId == null) ? "nil" : partyLeaderId));

        return partyLeaderId;
    }

    @Nullable
    public Party getPartyWithLeader(@NotNull Long leaderId) {
        LOGGER.info("getPartyWithLeader: leaderId=" + leaderId);

        final Party party = parties.getOrDefault(leaderId, null);
        LOGGER.info("        : party is " + ((party == null) ? "null" : "not null"));
        if (party != null) {
            LOGGER.info("        : party.leader.id=" + party.getLeader().getId());
        }

        return party;
    }

    @Nullable
    public Party getParty(@NotNull User user) {
        LOGGER.info("getParty: user.id=" + user.getId() + ", .login=" + user.getLogin());

        final Long leaderId = userIdToPartyId.getOrDefault(user.getId(), null);
        if (leaderId == null) {
            LOGGER.info("    : leader.id is NULL");
            return null;
        }

        return getPartyWithLeader(leaderId);
    }
}
