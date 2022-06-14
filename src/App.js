import React, { Component } from "react";
import "./App.css";
import Login from "./user/login/Login";
import LoginHome from "./LoginHome";
import Home from "./home";
import Search from "./Search";
import Signup from "./user/signup/Signup";
import Profile from "./user/profile/Profile";
import Collect from "./components/Collect";
import AppHeader from "./common/AppHeader";
import AppFooter from "./common/AppFooter";
import { ACCESS_TOKEN } from "./constants";
import { getCurrentUser } from "./util/APIUtils";
import { Route, withRouter, Switch } from "react-router-dom";
import { Layout, notification } from "antd";
import "./index.css";
const { Content } = Layout;

class App extends Component {
  constructor(props) {
    super(props);
    this.state = {
      currentUser: null,
      isAuthenticated: false,
      isLoading: false,
    };
    this.handleLogout = this.handleLogout.bind(this);
    this.loadCurrentUser = this.loadCurrentUser.bind(this);
    this.handleLogin = this.handleLogin.bind(this);

    notification.config({
      placement: "topRight",
      top: 70,
      duration: 3,
    });
  }
  loadCurrentUser() {
    this.setState({
      isLoading: true,
    });
    getCurrentUser()
      .then((response) => {
        this.setState({
          currentUser: response,
          isAuthenticated: true,
          isLoading: false,
        });
        this.props.history.push(`/accessHome/${response.username}`);
      })
      .catch((error) => {
        this.setState({
          isLoading: false,
        });
      });
  }

  componentWillMount() {
    this.loadCurrentUser();
  }

  handleLogout(
    redirectTo = "/login",
    notificationType = "success",
    description = "You're successfully logged out."
  ) {
    localStorage.removeItem(ACCESS_TOKEN);

    this.setState({
      currentUser: null,
      isAuthenticated: false,
    });

    this.props.history.push(redirectTo);

    notification[notificationType]({
      message: "Search App",
      description: description,
    });
  }

  handleLogin() {
    notification.success({
      message: "Search App",
      description: "You're successfully logged in.",
    });
    this.loadCurrentUser();
  }
  render() {
    return (
      <Layout>
        <AppHeader
          isAuthenticated={this.state.isAuthenticated}
          currentUser={this.state.currentUser}
          onLogout={this.handleLogout}
        />

        <Content className="app-content" style={{ marginTop: 100 }}>
          <div className="container" style={{ padding: 15 }}>
            <Switch>
              <Route
                path="/accessHome/:username"
                render={(props) => (
                  <LoginHome
                    isAuthenticated={this.state.isAuthenticated}
                    username={this.state.currentUser}
                    {...props}
                  />
                )}
              ></Route>
              <Route
                path="/login"
                render={(props) => (
                  <Login onLogin={this.handleLogin} {...props} />
                )}
              ></Route>
              <Route path="/signup" component={Signup}></Route>
              <Route
                path="/collect"
                render={(props) => (
                  <Collect id={this.state.currentUser.id} {...props} />
                )}
              ></Route>
              <Route path="/search" component={Search}></Route>
              <Route
                path="/users/:username"
                render={(props) => (
                  <Profile
                    isAuthenticated={this.state.isAuthenticated}
                    currentUser={this.state.currentUser}
                    {...props}
                  />
                )}
              ></Route>
              <Route path="/" component={Home}></Route>
            </Switch>
          </div>
        </Content>

        <AppFooter />
      </Layout>
    );
  }
}

export default withRouter(App);
