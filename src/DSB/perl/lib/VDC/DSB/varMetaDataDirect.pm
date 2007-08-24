package VDC::DSB::varMetaDataDirect; 
# 
# Copyright (C) 2007 President and Fellows of Harvard University
#	  (Written by Leonid Andreev)
#	  (<URL:http://thedata.org/>)
# 
# This program is free software; you can redistribute it and/or
# modify it under the terms of the GNU General Public License
# as published by the Free Software Foundation; either version 2
# of the License, or (at your option) any later version.
# 
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
# 
# You should have received a copy of the GNU General Public License
# along with this program; if not, write to the Free Software
# Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307,
# USA.
# 
# Redistributions of source code or binaries must retain the above copyright
# notice.
#

use DBI;

sub new {
	my $class = shift;
	my $self = { @_ };
	$self->{_varNameA}=[];
	$self->{_varNameAsafe}=[];
	$self->{_varNameH}={};
	$self->{_varNameHsafe}={};
	$self->{_varType}=[];
	$self->{_varLabel}=[];
	$self->{_varNo}=[];
	$self->{_varNoMpTbl}={};
	$self->{_valLblTbl}={};
	$self->{_mssvlTbl}={};
	$self->{_charVarTbl}={};
	$self->{_varFormat}={};
	$self->{_formatName}={};
	$self->{_formatCatgry}={};
	$self->{unsafeVarName}=0;

	$self->{censusURL} = "";
	# configuration -- here temporarily; 
	# should be moved someplace else

	$self->{sqlHost} = "localhost"; 
	$self->{sqlDB}   = "vdcNet-test";
	$self->{sqlUser} = "vdcApp";
	$self->{sqlPw}   = "secret";

	$novars = scalar(keys(%{$self->{VarID}}));
	
	bless $self, ref($class)||$class;
	return $self;
}


sub obtainMeta {
	my $self = shift @_;

	my $varID = $self->{VarID};
	my $fileID = $self->{FileID};

	
	my $sqlHost = $self->{sqlHost}; 
	my $sqlUser = $self->{sqlUser};
	my $sqlDB   = $self->{sqlDB};
	my $sqlPw   = $self->{sqlPw};
	
	my $dbh = DBI->connect("DBI:Pg:dbname=$sqlDB",$sqlUser,$sqlPw);

	
	# 1st lookup: find out the datatable id by the studyfile id 
	# supplied: 

	my $sth = $dbh->prepare(qq {SELECT id FROM datatable WHERE studyfile_id=$fileID});
	$sth->execute();

	my ($datatable_id) = $sth->fetchrow(); 

	$sth-finish; 

	# 2nd lookup: we can now look up the variables: 

	$sth = $dbh->prepare(qq {SELECT id,name,fileorder,label,variableformattype_id,variableintervaltype_id FROM datavariable WHERE datatable_id=$datatable_id ORDER BY fileorder});
	$sth->execute();

	my $var_id; 
	my $var_name; 
	my $var_order; 
	my $var_label; 
	my $var_format; 
	my $var_interval; 

	while ( ($var_id,$var_name,$var_order,$var_label,$var_format,$var_interval)
		= $sth->fetchrow() )
	{
	    # the variable id in the Dataverse notation: 

	    my $dv_var_id = "v" . $var_id; 

	    # check if the variable is among the ones requested: 

	    if ( $varID->{$dv_var_id} )
	    {
		$self->{_varNameH}->{$dv_var_id} = $var_name; 
		push @{$self->{_varNameA}}, $var_name; 
		$self->{_varNoMpTbl}->{$dv_var_id} = $var_order + 1; 
		push @{$self->{_varNo}}, $dv_var_id; 
		

		my $var_type; 

		if ( $var_interval == 2 ) 
		{
		    $var_type = 2; 
		}
		elsif ( $var_interval == 1 ) 
		{
		    if ( $var_format == 1 ) 
		    {
			$var_type = 1; 
		    }
		    elsif ( $var_format == 2 ) 
		    {
			$var_type = 0; 
		    }
		}

		push @{$self->{_varType}}, $var_type; 

		if ( $var_type == 0 ) 
		{
		    $self->{_charVarTbl}->{$dv_var_id} = 'y'; 
		}

		push @{$self->{_varLabel}}, $var_label; 

		# more lookups necessary, for discrete variables:

		if ( $var_type == 1 || $var_type == 0 )
		{
		    my $sth1 = $dbh->prepare(qq {SELECT id,label,value FROM variablecategory WHERE datavariable_id=$var_id});
		    $sth1->execute();
		
		    while ( my ($val_id, $val_label, $val_value) = $sth1->fetchrow() )
		    {
			if ( $val_label ne "" )
			{
			    $self->{_valLblTbl}->{$var_name} = [] unless $self->{_valLblTbl}->{$var_name}; 
			    push @{$self->{_valLblTbl}->{$var_name}}, [$val_value, $val_label]; 
			}
		    }
		    
		    $sth1->finish; 

		    my $sth1 = $dbh->prepare(qq {SELECT endvalue,beginvalue FROM variablerange WHERE datavariable_id=$var_id});
		    $sth1->execute();

		    while ( my ($endvalue, $beginvalue) = $sth1->fetchrow() )
		    {
			$self->{_mssvlTbl}->{$var_name} = [] unless $self->{_mssvlTbl}->{$var_name}; 
			push @{$self->{_mssvlTbl}->{$var_name}}, [$beginvalue]; 
		    }
		    
		    $sth1->finish; 
		}
	    }
	    else
	    { 
		# skip; 
	    }
	}

	$sth->finish; 



	# finally, one more check to see if this is a Census URL:

	my $sth = $dbh->prepare(qq {SELECT filesystemlocation FROM studyfile WHERE id=$fileID});

	$sth->execute();
	my ($location) = $sth->fetchrow(); 

	if ( $location =~/^http:\/\/.*census\.gov/i )
	{
	    $self->{censusURL} = $location; 
	}
	 
	$sth->finish; 
	
	$dbh->disconnect; 

	my $temp= {};

	while ((my $key, my $value) = each(%{$self})) {
		$temp->{$key} = $value;
	}

	# additional values: 

	$temp->{unsafeVarName} = 0; 
	
	return $temp;
}

1;

