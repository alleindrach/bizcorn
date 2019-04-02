/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow
 */
import React, { Component } from 'react';
import {Alert,View, TouchableHighlight, TouchableOpacity, TouchableNativeFeedback, TouchableWithoutFeedback,FlatList} from 'react-native';
import { Container, Header, Content, Input, Item,Thumbnail ,Button,Text} from 'native-base';
import {StompEventTypes, withStomp} from 'react-stompjs'


class MyListItem extends React.PureComponent {
  _onPress = () => {
    this.props.onPressItem(this.props.id);
  };

  render() {
    const textColor = this.props.selected ? "red" : "black";
    return (
      <TouchableOpacity onPress={this._onPress}>
        <View>
          <Text style={{ color: textColor }}>
            {this.props.title}
          </Text>
        </View>
      </TouchableOpacity>
    );
  }
}


class MultiSelectList extends React.PureComponent {
  state = {selected: (new Map())};

  _keyExtractor = (item, index) => item.id;

  _onPressItem = (id) => {
    // updater functions are preferred for transactional updates
    this.setState((state) => {
      // copy the map rather than modifying state.
      const selected = new Map(state.selected);
      selected.set(id, !selected.get(id)); // toggle
      return {selected};
    });
  };

  _renderItem = ({item}) => (
    <MyListItem
      id={item.id}
      onPressItem={this._onPressItem}
      selected={!!this.state.selected.get(item.id)}
      title={item.title}
    />
  );

  render() {
    return (
      <FlatList
        data={this.props.data}
        extraData={this.state}
        keyExtractor={this._keyExtractor}
        renderItem={this._renderItem}
      />
    );
  }
}

class App extends Component {
  counter=0;
  _onPressButton() {
    this._userLogin();
  }
  _captcha(){
    this.setState({captchaUrl:'http://127.0.0.1:8762/common/captcha.jpg?v='+new Date().getTime()});
  }
  _userLogin() {
    let username=this.state.username;
    let password=this.state.password;
    let captcha=this.state.captcha;
    return fetch('http://127.0.0.1:8762/user/login',{
      credentials: 'include',
      method: 'POST',
      headers: {
        Accept: 'application/json',
        'Content-Type': 'application/x-www-form-urlencoded',
        'X-Requested-With':"XMLHttpRequest"
      },
      body: 'username='+username+'&password='+password+'&captcha='+ captcha
      
    })
      
      .then((response) => {
        
        return response.json();
      })
      .then((responseJson) => {
        if(responseJson.state==1){
          alert('登录成功');
        }else
        {
          alert(responseJson.msg);
        }
        
      })
      .catch((error) => {
        console.error(error);
      });
  }
  constructor(props) {

    super(props);

    this.state={  status:'Not Connected',username:'allein',password: '123456',captcha: '' ,captchaUrl:'http://127.0.0.1:8762/common/captcha.jpg',
    groupname:"alleins",groupPoint:"/center/group/"+this.props.groupname,singlePoint:"/center/message",allmePoint:"/center/brodcast/message",
    singleSubPoint:"/user/topic/message",broadcastSubPoint:"/topic/message", groupSubPoint:"/group/"+this.props.groupname+"/message",
    messages:new Array()
  };
    // this._captcha();
  }
  componentDidMount(){
    this.props.stompContext.addStompEventListener(
        StompEventTypes.Connect,
        () => {
          console.log('connected!');
          this.setState({status: 'Connected'})
          this.state.stompclient.subscribe('/user/topic/message',(msg=>{
            this.counter++;
            const msgs = this.state.messages;
            // msgs.push({id:this.counter,title:msg.body}); 
            this.setState({messages:msgs.concat({key:msg.headers['message-id'],title:msg.body})})
             console.log(msg);
          }).bind(this))
        }
    )
    this.props.stompContext.addStompEventListener(
        StompEventTypes.Disconnect,
        () => {
          console.log('Disconnected!');
          this.setState({status: 'Disconnected'})
        }
    )
    this.props.stompContext.addStompEventListener(
        StompEventTypes.WebSocketClose,
        () => {
          console.log('Disconnected!');
          this.setState({status: 'Disconnected (not graceful)'})
        }
    )
    this.state.stompclient=this.props.stompContext.newStompClient(
        'http://127.0.0.1:8762/websocket')  // it's '/' most likely
  }
  _wsConnect(){
    this.state.stompclient.activate();
  }
  _wsSendMessage(){
    this.state.stompclient.publish({destination: this.state.singlePoint, body: JSON.stringify({msg:this.state.message})});
  }
  render() {
    return (
      <Container>
        <Header />
        <Content>
          <Item regular>
            <Input placeholder='Username' label='username' value={this.state.username} onChangeText={(text)=>this.setState({username:text})}/>
          </Item>
          <Item regular>
            <Input placeholder='Password' value={this.state.password} onChangeText={(text)=>this.setState({password:text})}/>
          </Item>
          <TouchableWithoutFeedback
          onPress={this._captcha.bind(this)}
          >
          <View >
            <Thumbnail square large source={{uri: this.state.captchaUrl}}  style={{height:150 ,width:400}} />
          </View>
          </TouchableWithoutFeedback>
          
          <Item regular>
            <Input placeholder='captcha' value={this.state.captcha} onChangeText={(text)=>this.setState({captcha:text})}/>
          </Item>
          <Button  full success={true} disabled={false} onPress={this._onPressButton.bind(this)}>
          <Text>login</Text>
        </Button>
        <View style={{with:100,height:10,backgroundColor:'pink'}}>
            
          </View>
        <Button  full success={true} disabled={false} onPress={this._wsConnect.bind(this)}>
          <Text>status:{this.state.status}</Text>
        </Button>
        <Item regular>
            <Input placeholder='message' value={this.state.message} onChangeText={(text)=>this.setState({message:text})}/>
          </Item>
         <Button  full success={true} disabled={false} onPress={this._wsSendMessage.bind(this)}>
          <Text>echo</Text>
        </Button>
        <FlatList
          data={this.state.messages}
          renderItem={
            ({item}) => 
            {return (<Text>{item.title}</Text>)}
        }
        />
        </Content>
      </Container>
    );
  }
}


export default withStomp(App);