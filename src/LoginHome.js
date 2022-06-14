import React, { Component } from "react";
import { getUserProfile } from "./util/APIUtils";
import LoadingIndicator from "./common/LoadingIndicator";
import NotFound from "./common/NotFound";
import ServerError from "./common/ServerError";
import CheckAuthentication from "./common/CheckAuthentication";
import Collect from "./components/Collect";

class LoginHome extends Component {
  constructor(props) {
    super(props);
    this.state = {
      user: null,
      isLoading: false,
      id:null,
      data:null,
    };
  }
  onSearch = () => {
    console.log("search");
  };
  loadUserProfile = (username) => {
    const _this = this;
    if (this.props.username !== null) {
      _this.setState({
        isLoading: true,
      });

      getUserProfile(username)
        .then((response) => {
          console.log(response)
          _this.setState({
            user: response,
            id:response.id,
            isLoading: false,
          });
        })
        .catch((error) => {
          if (error.status === 404) {
            _this.setState({
              notFound: true,
              isLoading: false,
            });
          } else {
            _this.setState({
              serverError: true,
              isLoading: false,
            });
          }
        });
    }
  
  };

  componentDidMount() {
    const username = this.props.match.params.username;
    this.loadUserProfile(username);
  }

  componentWillReceiveProps(nextProps) {
    if (this.props.match.params.username !== nextProps.match.params.username) {
      this.loadUserProfile(nextProps.match.params.username);
    }
  }

  render() {
    if (this.state.isLoading) {
      return <LoadingIndicator />;
    }

    if (this.state.notFound) {
      return <NotFound />;
    }

    if (this.state.serverError) {
      return <ServerError />;
    }
    if (!this.props.isAuthenticated) {
      return <CheckAuthentication {...this.props} />;
    }
    console.log("log this.props.match.params",this.props.match.params)
    return (
      <div>
        {this.state.user ? <div>
          <Collect id={this.state.id}/>
          </div>
           : null}
      </div>
    );
  }
}

export default LoginHome;
