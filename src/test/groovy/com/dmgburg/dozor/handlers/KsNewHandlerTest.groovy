package com.dmgburg.dozor.handlers

import com.dmgburg.dozor.ChatState
import com.dmgburg.dozor.ChatStateRepository
import com.dmgburg.dozor.KsRepository
import com.dmgburg.dozor.RolesRepository
import com.dmgburg.dozor.core.TgApi
import com.dmgburg.dozor.domain.Chat
import com.dmgburg.dozor.domain.Message
import com.dmgburg.dozor.domain.User
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mock
import org.mockito.MockitoAnnotations

import static com.dmgburg.dozor.security.Role.Team
import static com.dmgburg.dozor.security.Role.Unauthentificated
import static org.mockito.Mockito.*

class KsNewHandlerTest {

    Message message
    KsNewHandler handler
    @Mock
    ChatStateRepository chatStateRepository
    @Mock
    KsRepository ksRepository
    @Mock
    TgApi tgApi
    @Mock
    User user
    @Mock
    RolesRepository rolesRepository
    ArgumentCaptor<List<String>> captor = ArgumentCaptor.forClass(List)
    Chat chat

    @Before
    void setUp() {
        MockitoAnnotations.initMocks(this)
        chat = new Chat()
        chat.id = 1
        message = new Message()
        message.chat = chat
        message.from = user
        message.text = "random message"
        when(user.chat).thenReturn(chat)
        when(rolesRepository.getRoles(any(Chat))).thenReturn([Team])
        handler = new KsNewHandler(tgApi, ksRepository, chatStateRepository, rolesRepository)
    }

    @Test
    void "should set ks in ksRepository when ks given as argument"() {
        message.text = "/ksnew 1,2+,3,2,1+,null"
        handler.handle(message)
        verify(ksRepository).setKs(captor.capture())
        assert captor.getValue() == ["1", "2+", "3", "2", "1+", "null"]
    }

    @Test
    void "should set ks in ksRepository when ks given as next message"() {
        message.text = "1,2+,3,2,1+,null"
        when(chatStateRepository.getState(chat)).thenReturn(ChatState.KS_NEW)
        handler.handle(message)
        verify(ksRepository).setKs(captor.capture())
        assert captor.getValue() == ["1", "2+", "3", "2", "1+", "null"]
    }

    @Test
    void "should not set ks in ksRepository when user Unauthentificated"() {
        when(rolesRepository.getRoles(chat)).thenReturn([Unauthentificated])
        message.text = "1,2+,3,2,1+,null"
        when(chatStateRepository.getState(chat)).thenReturn(ChatState.KS_NEW)
        handler.handle(message)
        verify(ksRepository,never()).setKs(captor.capture())
    }

    @Test
    @Ignore
    void "should set chat state to ksnew when massage has no arguments"() {
        message.text = "/ksnew"
        handler.handle(message)
        verify(chatStateRepository).setState(eq(chat), eq(ChatState.KS_NEW))
    }

    @Test
    void "should not set ks in ksRepository when ks do not match pattern"() {
        message.text = "some random message"
        when(chatStateRepository.getState(chat)).thenReturn(ChatState.KS_NEW)
        handler.handle(message)
        verify(ksRepository, never()).setKs(captor.capture())
    }

    @Test
    void "should handle /ksNew"() {
        message.text = "/ksNew"
        assert handler.isHandled(message)
    }

    @Test
    @Ignore
    void "should handle all messages when ChatState is ksNew"() {
        when(chatStateRepository.getState(chat)).thenReturn(ChatState.KS_NEW)
        assert handler.isHandled(message)
    }
}